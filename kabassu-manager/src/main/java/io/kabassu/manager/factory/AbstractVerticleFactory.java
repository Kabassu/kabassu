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

import io.kabassu.manager.utils.JsonObjectUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.VerticleFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractVerticleFactory implements VerticleFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVerticleFactory.class);

  private static final String CONFIG_KEY = "config";
  private static final String OPTIONS_KEY = "options";

  @Override
  public boolean requiresResolve() {
    return true;
  }

  @Override
  public void resolve(String id, DeploymentOptions deploymentOptions, ClassLoader classLoader,
      Future<String> resolution) {
    String identifier = VerticleFactory.removePrefix(id);
    String descriptorFile = identifier + ".json";
    try {
      JsonObject descriptor = readDescriptor(classLoader, descriptorFile);
      String verticle = retrieveVerticle(descriptor, descriptorFile);

      // Any options specified in the module config will override anything specified at deployment time
      // Options and Config specified in separate file with configuration JSON will override those configurations
      JsonObject depOptions = deploymentOptions.toJson();
      JsonObject depConfig = depOptions.getJsonObject(CONFIG_KEY, new JsonObject());

      JsonObject verticleOptions = descriptor.getJsonObject(OPTIONS_KEY, new JsonObject());
      JsonObject verticleConfig = verticleOptions.getJsonObject(CONFIG_KEY, new JsonObject());
      depOptions.mergeIn(verticleOptions);
      depOptions.put(CONFIG_KEY, JsonObjectUtil.deepMerge(verticleConfig, depConfig));

      updateOption(depOptions, "runmode", System.getProperty("runmode"));
      updateOption(depOptions, "security", System.getProperty("security"));

      JsonObject serviceDescriptor = new JsonObject().put(OPTIONS_KEY, depOptions);

      deploymentOptions.fromJson(serviceDescriptor.getJsonObject(OPTIONS_KEY));
      if (isWorker()) {
        //TODO Configurable worker instances and pool size
        deploymentOptions.setWorker(true);
        deploymentOptions.setWorkerPoolName(identifier);
        deploymentOptions.setWorkerPoolSize(5);
        deploymentOptions.setInstances(5);
        LOGGER.info("{} is worker", verticle);
      }
      resolution.complete(verticle);
    } catch (Exception e) {
      resolution.fail(e);
    }
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
    throw new IllegalStateException("Shouldn't be called");
  }

  protected String retrieveVerticle(JsonObject descriptor, String descriptorFile) {
    String verticle = descriptor.getString("verticle");
    if (StringUtils.isEmpty(verticle)) {
      throw new IllegalArgumentException("There is no verticle name in: " + descriptorFile);
    }
    return verticle;
  }

  protected abstract boolean isWorker();

  private void updateOption(JsonObject depOptions, String optionName, String option) {
    if (StringUtils.isNotEmpty(option)) {
      depOptions.getJsonObject(CONFIG_KEY).put(optionName, option);
    }
  }

  private JsonObject readDescriptor(ClassLoader classLoader, String descriptorFile)
      throws IOException {
    JsonObject descriptor;
    try (InputStream is = classLoader.getResourceAsStream(descriptorFile)) {
      if (is == null) {
        throw new IllegalArgumentException(
            "Cannot find module descriptor file " + descriptorFile + " on classpath");
      }
      try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
        String conf = scanner.next();
        descriptor = new JsonObject(conf);
      } catch (NoSuchElementException e) {
        throw new IllegalArgumentException(descriptorFile + " is empty", e);
      } catch (DecodeException e) {
        throw new IllegalArgumentException(descriptorFile + " contains invalid json", e);
      }
    }

    return descriptor;
  }

}
