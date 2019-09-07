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

package io.kabassu.runner.gradle.configuration;

import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class KabassuRunnerGradleConfiguration {

  private String address;
  private Map<String, String> jvmsMap = new HashMap<>();

  public KabassuRunnerGradleConfiguration(JsonObject configuration) {
    this.address = configuration.getString("address");
    JsonObject jvm = configuration.getJsonObject("jvm");
    jvm.stream().forEach(jvmEntry -> jvmsMap.put(jvmEntry.getKey(), jvmEntry.getValue().toString()));
  }

  public String getAddress() {
    return address;
  }

  public Map<String, String> getJvmsMap() {
    return jvmsMap;
  }
}
