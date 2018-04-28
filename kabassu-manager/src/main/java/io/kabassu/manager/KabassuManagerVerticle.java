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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;


public class KabassuManagerVerticle extends AbstractVerticle {

  private static final String CONFIG_OVERRIDE = "config";
  private static final String MODULE_OPTIONS = "options";
  private static final Logger LOGGER = LoggerFactory.getLogger(KabassuManagerVerticle.class);
  private static final String RUNMODE = "runmode";

  private ConfigRetriever configRetriever;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    updateOption(config(), RUNMODE, System.getProperty(RUNMODE));
    updateOption(config(), "security", System.getProperty("security"));

    configRetriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions(config().getJsonObject("modules")));

//    configRetriever.listen(conf -> {
//      if (!deployedModules.isEmpty()) {
//        LOGGER.warn("Configuration changed - Re-deploying Knot.x");
//        Observable.fromIterable(deployedModules)
//            .flatMap(item -> vertx.rxUndeploy(item.getDeploymentId()).toObservable())
//            .collect(() -> Lists.newArrayList(),
//                (collector, result) -> collector.add(result))
//            .subscribe(
//                success -> {
//                  LOGGER.warn("Knot.x STOPPED.");
//                  deployVerticles(conf.getNewConfiguration(), null);
//                },
//                error -> {
//                  LOGGER.error("Unable to undeploy verticles", error);
//                  startFuture.fail(error);
//                }
//            );
//      }
//    });

    configRetriever.listen(conf->{
      LOGGER.info(conf.getNewConfiguration());
    });

    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        JsonObject configuration = ar.result();
        LOGGER.info(configuration);
        //deployVerticles(configuration, startFuture);
      } else {
        LOGGER.fatal("Unable to start Knot.x", ar.cause());
        startFuture.fail(ar.cause());
      }
    });


//    Observable.fromIterable(config().getJsonArray("modules"))
//        .flatMap(module -> deployVerticle(module)
//            .onErrorResumeNext(
//                throwable -> (Observable<Pair<String, String>>) verticleCouldNotBeDeployed(module,
//                    throwable))
//        )
//        .compose(joinDeployments())
//        .subscribe(
//            message -> {
//              LOGGER.info("Kabassu STARTED {}", message);
//              if(config().getJsonObject(CONFIG_OVERRIDE).getString(RUNMODE,"normal").equalsIgnoreCase("demo")){
//                LOGGER.info("Kabassu is running in DEMO Mode");
//              }
//              startFuture.complete();
//            },
//            error -> {
//              LOGGER.error("Verticle could not be deployed", error);
//              startFuture.fail(error);
//            }
//        );
 }

  private Observable<Pair<String, String>> verticleCouldNotBeDeployed(Object module,
      Throwable throwable) {
    LOGGER.warn("Can't deploy {}: {}", module, throwable.getMessage());
    return Observable.empty();
  }

  private Observable<Pair<String, String>> deployVerticle(final Object module) {
    return vertx.rxDeployVerticle((String) module, getModuleOptions((String) module))
        .map(deploymentID -> Pair.of((String) module, deploymentID))
        .toObservable();
  }

  private DeploymentOptions getModuleOptions(final String module) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    //TODO: Other way for providing custom configuration
    if (config().containsKey(CONFIG_OVERRIDE) && config().getJsonObject(CONFIG_OVERRIDE)
        .containsKey(module)) {
      JsonObject moduleConfig = config().getJsonObject(CONFIG_OVERRIDE).getJsonObject(module);
      if (moduleConfig.containsKey(MODULE_OPTIONS)) {
        deploymentOptions.fromJson(moduleConfig.getJsonObject(MODULE_OPTIONS));
      }
    } else {
      deploymentOptions.fromJson(config());
    }
    return deploymentOptions;
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
    return accumulator
        .append(
            String.format("\t\tDeployed %s [%s]", deploymentId.getRight(), deploymentId.getLeft()))
        .append(System.lineSeparator());
  }

  private void updateOption(JsonObject depOptions, String optionName, String option) {
    if (StringUtils.isNotEmpty(option)) {
      depOptions.getJsonObject(CONFIG_OVERRIDE).put(optionName, option);
    }
  }
}
