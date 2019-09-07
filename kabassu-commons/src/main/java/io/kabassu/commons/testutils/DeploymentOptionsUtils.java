/*
 *   Copyright (C) 2018 Kabassu
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.kabassu.commons.testutils;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class DeploymentOptionsUtils {

  private static final String CONFIG_KEY = "config";
  private static final String OPTIONS_KEY = "options";

  public static DeploymentOptions createDeploymentOptionsFromJson(String json) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    JsonObject depOptions = deploymentOptions.toJson();
    depOptions.put(CONFIG_KEY, new JsonObject(json));
    JsonObject serviceDescriptor = new JsonObject().put(OPTIONS_KEY, depOptions);
    deploymentOptions.fromJson(serviceDescriptor.getJsonObject(OPTIONS_KEY));
    return deploymentOptions;
  }

  public static DeploymentOptions createDeploymentOptionsFromJson(JsonObject json) {
    DeploymentOptions deploymentOptions = new DeploymentOptions();
    JsonObject depOptions = deploymentOptions.toJson();
    depOptions.put(CONFIG_KEY, json);
    JsonObject serviceDescriptor = new JsonObject().put(OPTIONS_KEY, depOptions);
    deploymentOptions.fromJson(serviceDescriptor.getJsonObject(OPTIONS_KEY));
    return deploymentOptions;
  }

  private DeploymentOptionsUtils() {
    //for static methods
  }
}
