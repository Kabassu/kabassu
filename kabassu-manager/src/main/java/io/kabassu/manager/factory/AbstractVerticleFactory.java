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
package io.kabassu.manager.factory;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.VerticleFactory;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractVerticleFactory implements VerticleFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVerticleFactory.class);

  @Override
  public boolean requiresResolve() {
    return true;
  }

  public void resolve(String id, DeploymentOptions deploymentOptions, ClassLoader classLoader,
      Promise<String> resolution) {
    String identifier = VerticleFactory.removePrefix(id);
    try {
      String verticle = retrieveVerticle(deploymentOptions, identifier);

      if (isWorker()) {
        deploymentOptions.setWorker(true);
        deploymentOptions.setWorkerPoolName(identifier);
        deploymentOptions.setWorkerPoolSize(5);
        deploymentOptions.setInstances(5);
        LOGGER.info("{} is worker", verticle);
      }
      //ModuleJarLoader.checkClasspath(deploymentOptions.getConfig().getString("modules_directory"));
      resolution.complete(verticle);
    } catch (Exception e) {
      resolution.fail(e);
    }
  }


  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
    throw new IllegalStateException("Shouldn't be called");
  }

  protected String retrieveVerticle(DeploymentOptions deploymentOptions, String id) {
    String verticle = deploymentOptions.getConfig().getString("verticle");
    if (StringUtils.isEmpty(verticle)) {
      throw new IllegalArgumentException("There is no verticle cconnected to: " + id);
    }
    return verticle;
  }

  protected abstract boolean isWorker();

}
