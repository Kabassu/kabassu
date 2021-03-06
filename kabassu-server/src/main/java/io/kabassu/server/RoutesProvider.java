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
package io.kabassu.server;

import io.kabassu.server.configuration.KabassuServerConfiguration;
import io.kabassu.server.configuration.RoutingPath;
import io.kabassu.server.handlers.ServerRoutingHandlersFactory;
import io.kabassu.server.security.jwt.JWTProvider;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import io.vertx.reactivex.ext.web.handler.ErrorHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;

class RoutesProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutesProvider.class);
  private final Vertx vertx;
  private final KabassuServerConfiguration options;

  RoutesProvider(Vertx vertx, KabassuServerConfiguration options) {
    this.vertx = vertx;
    this.options = options;
    validateRoutingOperations();
  }

  void configureRouting(OpenAPI3RouterFactory routerFactory) {
    ServerRoutingHandlersFactory handlerFactory = new ServerRoutingHandlersFactory(vertx, options);
    routerFactory.addGlobalHandler(
      CorsHandler.create("*").allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST)
        .allowedMethod(HttpMethod.OPTIONS).allowedHeader("*"));

    routerFactory.addSecurityHandler("bearerAuth",
      JWTAuthHandler.create(JWTProvider.getProvider()));

    options.getRoutingPath().forEach(operation -> {
      registerRoutingHandlersPerOperation(routerFactory, handlerFactory, operation);
      routerFactory
        .addFailureHandlerByOperationId(operation.getOperationId(), ErrorHandler.create(true));
      LOGGER.info("Initialized all handlers for operation [{}]", operation.getOperationId());
    });
  }

  private void validateRoutingOperations() {
    if (options.getRoutingPath() == null || options.getRoutingPath().isEmpty()) {
      LOGGER.warn(
        "The server configuration does not contain any operation defined. Please check your "
          + "configuration [io.kabassu.server.json -> config.operations]");
    }
  }

  private void registerRoutingHandlersPerOperation(OpenAPI3RouterFactory routerFactory,
    ServerRoutingHandlersFactory handlersFactory, RoutingPath operation) {
    routerFactory.addHandlerByOperationId(operation.getOperationId(),
      handlersFactory.createRoutingHandler(operation.getHandler(), operation.getAddress()));
  }

}
