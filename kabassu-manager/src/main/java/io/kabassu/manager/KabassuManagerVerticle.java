/*
 * Copyright (C) 2016 Cognifide Limited
 * Modified (C) 2018 Kabassu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kabassu.manager;

import io.kabassu.manager.deployment.DeployStatus;
import io.kabassu.manager.deployment.DeploymentManager;
import io.kabassu.manager.deployment.ModuleDeployInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;


public class KabassuManagerVerticle extends AbstractVerticle {

  private static final String CONFIG_OVERRIDE = "config";

  private static final String MODULE_KEY = "modules";

  private static final Logger LOGGER = LoggerFactory.getLogger(KabassuManagerVerticle.class);

  private static final String RUNMODE = "runmode";

  public static final String SECURITY = "security";

  private DeploymentManager deploymentManager;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    updateOption(config().getJsonObject(CONFIG_OVERRIDE), RUNMODE, System.getProperty(RUNMODE), true);
    updateOption(config().getJsonObject(CONFIG_OVERRIDE), SECURITY, System.getProperty(SECURITY), true);

    ConfigRetriever configRetriever = ConfigRetriever
        .create(vertx, new ConfigRetrieverOptions(config().getJsonObject(MODULE_KEY)));

    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        JsonObject configuration = ar.result();
        deploymentManager = new DeploymentManager(configuration.getJsonArray(MODULE_KEY));
        deployVerticles(startFuture, deploymentManager.getDeployedModules().keySet());
      } else {
        LOGGER.fatal("Unable to start Kabbasu", ar.cause());
        startFuture.fail(ar.cause());
      }
    });

    configRetriever.listen(conf ->
        redeployVerticles(deploymentManager
            .refreshDeploys(conf.getNewConfiguration().getJsonArray(MODULE_KEY)), startFuture)
    );
  }

  private void redeployVerticles(List<ModuleDeployInfo> moduleDeployInfos,
      Future<Void> startFuture) {
    Observable.fromIterable(moduleDeployInfos)
        .filter(
            moduleDeployInfo -> moduleDeployInfo.getDeployStatus().equals(DeployStatus.UNDEPLOY)
                || moduleDeployInfo.getDeployStatus().equals(DeployStatus.REDEPLOY))
        .flatMap(moduleDeployInfo -> {
          LOGGER.info("Undeploying: " + moduleDeployInfo.getName());
          return vertx
              .rxUndeploy(moduleDeployInfo.getDeploymentId())
              .toObservable();
        })
        .subscribe(
            success ->
                LOGGER.warn("Undeployment completed")
            ,
            error -> {
              LOGGER.error("Unable to undeploy verticles", error);
              startFuture.fail(error);
            }
        );
    new HashSet<>();

    Set<String> modulesToDeploy = moduleDeployInfos.stream()
        .filter(moduleDeployInfo -> moduleDeployInfo.getDeployStatus().equals(DeployStatus.DEPLOY)
            || moduleDeployInfo.getDeployStatus().equals(DeployStatus.REDEPLOY))
        .map(ModuleDeployInfo::getName).collect(Collectors.toSet());

    if (!modulesToDeploy.isEmpty()) {
      deployVerticles(null, modulesToDeploy);
    }
  }

  private void deployVerticles(Future<Void> startFuture, Set<String> modules) {
    Observable.fromIterable(modules)
        .flatMap(module -> deployVerticle(module)
            .onErrorResumeNext(
                throwable -> (Observable<Pair<String, String>>) verticleCouldNotBeDeployed(module,
                    throwable))
        )
        .compose(joinDeployments())
        .subscribe(
            message -> {
              LOGGER.info("Kabassu STARTED {}", message);
              if (config().getJsonObject(CONFIG_OVERRIDE).getString(RUNMODE, "normal")
                  .equalsIgnoreCase("demo")) {
                LOGGER.info("Kabassu is running in DEMO Mode");
              }
              if (startFuture != null) {
                startFuture.complete();
              }
            },
            error -> {
              LOGGER.error("Verticle could not be deployed", error);
              if (startFuture != null) {
                startFuture.fail(error);
              }
            }
        );

  }

  private Observable<Pair<String, String>> verticleCouldNotBeDeployed(Object module,
      Throwable throwable) {
    LOGGER.warn("Can't deploy {}: {}", module, throwable.getMessage());
    deploymentManager.getDeployedModules().remove(module);
    return Observable.empty();
  }

  private Observable<Pair<String, String>> deployVerticle(final Object module) {

    DeploymentOptions deploymentOptions = new DeploymentOptions();
    deploymentOptions.fromJson(deploymentManager.getDeployedModules().get(module).copy());
    updateOption(deploymentOptions.getConfig(), RUNMODE,
        config().getJsonObject(CONFIG_OVERRIDE).getString(RUNMODE), false);
    updateOption(deploymentOptions.getConfig(), SECURITY,
        config().getJsonObject(CONFIG_OVERRIDE).getString(SECURITY), false);
    return vertx.rxDeployVerticle((String) module, deploymentOptions)
        .map(deploymentID -> Pair.of((String) module, deploymentID))
        .toObservable();
  }

  private ObservableTransformer<Pair<String, String>, String> joinDeployments() {
    return observable ->
        observable.reduce(new StringBuilder(System.lineSeparator()).append(System.lineSeparator()),
            this::collectDeployment)
            .toObservable()
            .map(StringBuilder::toString);
  }

  private StringBuilder collectDeployment(StringBuilder accumulator,
      Pair<String, String> deploymentId) {
    deploymentManager.getDeployedModules().get(deploymentId.getLeft())
        .put(DeploymentManager.DEPLOYMENT_ID, deploymentId.getRight());
    return accumulator
        .append(
            String.format("\t\tDeployed %s [%s]", deploymentId.getRight(), deploymentId.getLeft()))
        .append(System.lineSeparator());
  }

  private void updateOption(JsonObject depOptions, String optionName, String option,
      boolean override) {
    if (StringUtils.isNotEmpty(option) && (
        !depOptions.containsKey(optionName) || override)) {
      depOptions.put(optionName, option);
    }
  }
}
