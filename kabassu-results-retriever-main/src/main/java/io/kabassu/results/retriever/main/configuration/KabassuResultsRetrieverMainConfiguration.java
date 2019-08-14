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

package io.kabassu.results.retriever.main.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

public class KabassuResultsRetrieverMainConfiguration {

  private String address;
  private String defaultReports;
  private String defaultReportsDir;
  private Map<String, String> reportsTypes = new HashMap<>();

  public KabassuResultsRetrieverMainConfiguration(JsonObject configuration) {
    this.address = configuration.getString("address");
    this.defaultReports = configuration.getString("defaultReports");
    this.defaultReportsDir = configuration.getString("defaultReportsDir");
    JsonArray reports = configuration.getJsonArray("reports");
    reports.stream().forEach(reportConfig -> {
      JsonObject reportJson = (JsonObject) reportConfig;
      reportsTypes.put(reportJson.getString("id"), reportJson.getString("retriever"));
    });
  }

  public String getDefaultReports() {
    return defaultReports;
  }

  public String getAddress() {
    return address;
  }

  public String getDefaultReportsDir() {
    return defaultReportsDir;
  }

  public Map<String, String> getReportsTypes() {
    return reportsTypes;
  }
}
