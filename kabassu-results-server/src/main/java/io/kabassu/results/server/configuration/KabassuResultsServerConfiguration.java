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

package io.kabassu.results.server.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class KabassuResultsServerConfiguration {

  private int port;

  private String resultsDir;

  public KabassuResultsServerConfiguration(JsonObject configuration) {
    this.port = configuration.getInteger("port");
    this.resultsDir = configuration.getString("resultsDir");
  }

  public int getPort() {
    return port;
  }

  public String getResultsDir() {
    return resultsDir;
  }
}
