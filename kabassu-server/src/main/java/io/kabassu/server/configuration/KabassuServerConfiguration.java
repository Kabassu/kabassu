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

package io.kabassu.server.configuration;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class KabassuServerConfiguration {

  private int port;

  private List<RoutingPath> routingPath = new ArrayList<>();

  public KabassuServerConfiguration(JsonObject configuration) {
    this.port = configuration.getInteger("port");
    mapPaths(configuration.getJsonArray("GET"), HttpMethod.GET);
    mapPaths(configuration.getJsonArray("POST"), HttpMethod.POST);
  }


  private void mapPaths(JsonArray paths, HttpMethod method) {
    if (paths != null) {
      paths.stream().map(path -> (JsonObject) path).forEach(pathData ->
          routingPath.add(
              new RoutingPath(method, pathData.getString("path"), pathData.getString("address"),
                  pathData.getString("handler",
                      StringUtils.EMPTY)))
      );
    }
  }

  public int getPort() {
    return port;
  }

  public List<RoutingPath> getRoutingPath() {
    return routingPath;
  }
}
