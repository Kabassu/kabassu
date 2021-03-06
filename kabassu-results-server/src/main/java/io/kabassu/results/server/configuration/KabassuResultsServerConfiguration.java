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

package io.kabassu.results.server.configuration;

import io.vertx.core.json.JsonObject;

public class KabassuResultsServerConfiguration {

  private final String certificatePath;

  private final String password;

  private int port;

  private String resultsDir;

  public KabassuResultsServerConfiguration(JsonObject configuration) {
    this.certificatePath = configuration.getString("cerificatePath");
    this.password = configuration.getString("password");
    this.port = configuration.getInteger("port");
    this.resultsDir = configuration.getString("resultsDir");
  }

  public String getCertificatePath() {
    return certificatePath;
  }

  public String getPassword() {
    return password;
  }

  public int getPort() {
    return port;
  }

  public String getResultsDir() {
    return resultsDir;
  }
}
