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

package io.kabassu.testcontext.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class KabassuTestContextConfiguration {

  private Map<String, RunnerConfig> runnersMap = new HashMap<>();

  private boolean masterServantModeAll;

  public KabassuTestContextConfiguration(JsonObject configuration) {
    masterServantModeAll = configuration.getBoolean("masterServantModeAll", false);
    JsonArray runners = configuration.getJsonArray("runners");
    runners.stream().forEach(runnerConfig -> {
      JsonObject runnerJson = (JsonObject) runnerConfig;
      runnersMap.put(runnerJson.getString("runner"),
        new RunnerConfig(runnerJson.getString("address"), runnerJson.getBoolean("servant", false)));
    });
  }

  public Map<String, RunnerConfig> getRunnersMap() {
    return runnersMap;
  }

  public boolean isMasterServantModeAll() {
    return masterServantModeAll;
  }
}
