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

package io.kabassu.server.handlers;

import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public class ServerRoutingHandlersFactory {

  private Vertx vertx;

  public ServerRoutingHandlersFactory(Vertx vertx) {
    this.vertx = vertx;
  }

  public Handler<RoutingContext> createRoutingHandler(String routingHandlerType, String address) {

    if (StringUtils.isEmpty(routingHandlerType) || routingHandlerType.equalsIgnoreCase("default")) {
      return new DefaultServerRoutingHandler(vertx, address);
    } else {
      if (routingHandlerType.equalsIgnoreCase("available")) {
        return new AvailableServerRoutingHandler(vertx, address);
      }

      if (routingHandlerType.equalsIgnoreCase("results")) {
        return new ResultsServerRoutingHandler(vertx, address);
      }
      if (routingHandlerType.equalsIgnoreCase("testmanager")) {
        return new TestManagerServerRoutingHandler(vertx, address);
      }
      if (routingHandlerType.equalsIgnoreCase("adddefinition")) {
        return new AddDefinitionRoutingHandler(vertx, address);
      }
    }

    throw new IllegalArgumentException("Unknown type of handler: " + routingHandlerType);
  }

}
