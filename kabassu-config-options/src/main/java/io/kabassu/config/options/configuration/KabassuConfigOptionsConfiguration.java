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

package io.kabassu.config.options.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class KabassuConfigOptionsConfiguration {

  private Map<String, String[]> availableOptions = new HashMap<>();

  public KabassuConfigOptionsConfiguration(JsonObject configuration) {
    JsonArray runners = configuration.getJsonArray("configurations");
    runners.stream().forEach(runnerConfig -> {
      JsonObject runnerJson = (JsonObject) runnerConfig;
      availableOptions.put(runnerJson.getString("fileName"), convertToArray(runnerJson.getJsonArray("options")));
    });
  }

  private String[] convertToArray(JsonArray options) {
    return new String[0];
  }

  public Map<String, String[]> getAvailableOptions() {
    return availableOptions;
  }
}
