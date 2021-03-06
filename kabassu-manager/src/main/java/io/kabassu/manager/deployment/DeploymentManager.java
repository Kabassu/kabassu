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

package io.kabassu.manager.deployment;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class DeploymentManager {

  public static final String DEPLOYMENT_ID = "deploymentId";

  private static final String CONFIG = "config";

  private Map<String, JsonObject> deployedModules = new HashMap<>();

  public DeploymentManager(JsonArray deployments) {
    deployments.iterator().forEachRemaining(this::addModule);
  }

  public List<ModuleDeployInfo> refreshDeploys(JsonArray deployments) {
    List<ModuleDeployInfo> toReturn = new ArrayList<>();

    Map<String, JsonObject> refreshedModules = new HashMap<>();
    deployments.iterator().forEachRemaining(deployment -> {
      JsonObject module = (JsonObject) deployment;
      refreshedModules
          .put(module.getString("name"),
              new JsonObject().put(CONFIG, module.getJsonObject(CONFIG, new JsonObject())));
    });
    prepareModulesForUndeploy(toReturn, refreshedModules);
    prepareModulesForDeploy(toReturn, refreshedModules);
    deployedModules = refreshedModules;
    return toReturn;
  }

  public Map<String, JsonObject> getDeployedModules() {
    return deployedModules;
  }

  private void prepareModulesForDeploy(List<ModuleDeployInfo> toReturn,
      Map<String, JsonObject> refreshedModules) {
    refreshedModules.keySet().stream().forEach(module -> {
      if (deployedModules.keySet().contains(module)) {
        if (!deployedModules.get(module).getJsonObject(CONFIG)
            .equals(refreshedModules.get(module).getJsonObject(CONFIG))) {
          refreshedModules.get(module)
              .put(DEPLOYMENT_ID,
                  deployedModules.get(module).getString(DEPLOYMENT_ID, StringUtils.EMPTY));
          toReturn
              .add(new ModuleDeployInfo(module, DeployStatus.REDEPLOY,
                  deployedModules.get(module).getString(DEPLOYMENT_ID, StringUtils.EMPTY)));
        }
      } else {
        toReturn
            .add(new ModuleDeployInfo(module, DeployStatus.DEPLOY, StringUtils.EMPTY));
      }
    });
  }

  private void prepareModulesForUndeploy(List<ModuleDeployInfo> toReturn,
      Map<String, JsonObject> refreshedModules) {
    Set<String> existingModules = new HashSet<>(deployedModules.keySet());
    existingModules.removeAll(refreshedModules.keySet());
    existingModules.stream().forEach(module ->
        toReturn
            .add(new ModuleDeployInfo(module, DeployStatus.UNDEPLOY,
                deployedModules.get(module).getString(DEPLOYMENT_ID, StringUtils.EMPTY)))

    );
  }


  private void addModule(Object entry) {
    JsonObject module = (JsonObject) entry;
    deployedModules.put(module.getString("name"),
        new JsonObject().put(CONFIG, module.getJsonObject(CONFIG, new JsonObject())));
  }


}
