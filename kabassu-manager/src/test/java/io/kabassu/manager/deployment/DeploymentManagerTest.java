/*
 * Copyright (C) 2018 Kabassu
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

package io.kabassu.manager.deployment;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class DeploymentManagerTest {

  @Test
  void testRefreshDeploys() {
    DeploymentManager deploymentManager = new DeploymentManager(getDeployments());
    List<ModuleDeployInfo> moduleDeployInfos = deploymentManager
        .refreshDeploys(getRefreshedDeployments());
    assertThat(moduleDeployInfos.size(),is(3));
    moduleDeployInfos.stream().forEach(deployInfo->{
      if(deployInfo.getName().equals("deploymentB")){
        assertThat(deployInfo.getDeployStatus(), is(DeployStatus.REDEPLOY));
        assertThat(deployInfo.getConfig().getString("test"),is("new2"));
      }
      if(deployInfo.getName().equals("deploymentC")){
        assertThat(deployInfo.getDeployStatus(), is(DeployStatus.UNDEPLOY));
      }
      if(deployInfo.getName().equals("deploymentD")){
        assertThat(deployInfo.getDeployStatus(), is(DeployStatus.DEPLOY));
      }
    });
    assertThat(deploymentManager.getDeployedModules().keySet().size(),is(3));
    assertThat(deploymentManager.getDeployedModules().get("deploymentA"),is(new JsonObject()));
    assertThat(deploymentManager.getDeployedModules().get("deploymentB"),is(new JsonObject().put("test", "new2")));
    assertThat(deploymentManager.getDeployedModules().get("deploymentD"),is(new JsonObject()));
    assertNull(deploymentManager.getDeployedModules().get("deploymentC"));
  }

  private JsonArray getDeployments() {
    JsonObject deploymentA = new JsonObject();
    deploymentA.put("name", "deploymentA");
    JsonObject deploymentB = new JsonObject()
        .put("name", "deploymentB")
        .put("config", new JsonObject().put("test", "new"));
    JsonObject deploymentC = new JsonObject();
    deploymentC.put("name", "deploymentC");
    return new JsonArray()
        .add(deploymentA)
        .add(deploymentB)
        .add(deploymentC);
  }

  private JsonArray getRefreshedDeployments() {
    JsonObject deploymentA = new JsonObject();
    deploymentA.put("name", "deploymentA");
    JsonObject deploymentB = new JsonObject()
        .put("name", "deploymentB")
        .put("config", new JsonObject().put("test", "new2"));
    JsonObject deploymentD = new JsonObject();
    deploymentD.put("name", "deploymentD");
    return new JsonArray()
        .add(deploymentA)
        .add(deploymentB)
        .add(deploymentD);
  }
}