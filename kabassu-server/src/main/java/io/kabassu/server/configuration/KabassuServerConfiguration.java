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

package io.kabassu.server.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class KabassuServerConfiguration {

  private int port;

  private List<RoutingPath> routingPath = new ArrayList<>();

  private String mainSecurityMode;

  private String certificatePath;

  private String password;

  private String routingSpecificationLocation;

  private String jwtSecret;

  private Integer expiresInMinutes;

  public KabassuServerConfiguration(JsonObject configuration) {
    this.certificatePath = configuration.getString("cerificatePath");
    this.password = configuration.getString("password");
    this.port = configuration.getInteger("port");
    this.mainSecurityMode = configuration.getString("security");
    mapOperations(configuration.getJsonArray("operations"));
    this.routingSpecificationLocation = configuration
        .getString("routingSpecificationLocation", "openapi/kabassu_api.yaml");
    this.jwtSecret = configuration.getString("jwtSecret");
    this.expiresInMinutes = configuration.getInteger("expiresInMinutes");
  }

  private void mapOperations(JsonArray operations) {
    if (operations != null) {
      operations.stream().map(operation -> (JsonObject) operation).forEach(pathData ->
          routingPath.add(
              new RoutingPath(pathData.getString("operationId"), pathData.getString("address"),
                  pathData.getString("handler",
                      StringUtils.EMPTY)))
      );
    }
  }

  public Integer getExpiresInMinutes() {
    return expiresInMinutes;
  }

  public String getJwtSecret() {
    return jwtSecret;
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

  public List<RoutingPath> getRoutingPath() {
    return routingPath;
  }

  public String getRoutingSpecificationLocation() {
    return routingSpecificationLocation;
  }

  public String getMainSecurityMode() {
    return mainSecurityMode;
  }
}
