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

package io.kabassu.server;

import io.kabassu.server.configuration.KabassuServerConfiguration;
import io.kabassu.server.handlers.ServerRoutingHandlersFactory;
import io.kabassu.server.security.SecurityHandlerFactory;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;

public class KabassuServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KabassuServerVerticle.class);

  private KabassuServerConfiguration options;

  private ServerRoutingHandlersFactory handlersFactory;

  private SecurityHandlerFactory securityHandlerFactory;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    options = new KabassuServerConfiguration(config());
  }

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    LOGGER.info("Open API specification location [{}]",
        options.getRoutingSpecificationLocation());

    HttpServerProvider httpServerProvider = new HttpServerProvider(vertx,options.getPort());
    RoutesProvider routesProvider = new RoutesProvider(vertx,options.getRoutingPath());

    OpenAPI3RouterFactory.rxCreate(vertx, options.getRoutingSpecificationLocation())
        .doOnSuccess(routesProvider::configureRouting)
        .map(OpenAPI3RouterFactory::getRouter)
        .doOnSuccess(this::logRouterRoutes)
        .flatMap(httpServerProvider::configureHttpServer)
        .subscribe(
            ok -> {
              LOGGER.info("Kabassu Server started. Listening on port {}",
                  options.getPort());
              startFuture.complete();
            },
            error -> {
              LOGGER.error("Unable to start Kabassu Server.", error.getCause());
              startFuture.fail(error);
            }
        );

  }

  private void logRouterRoutes(Router router) {
    LOGGER.info("Routes [{}]", router.getRoutes());
    printRoutes(router);
  }

  private void printRoutes(Router router) {
    // @formatter:off
    System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    System.out.println("@@                              ROUTER CONFIG                                 @@");
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    router.getRoutes().forEach(route -> System.out.println("@@     " + route.getDelegate()));
    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
    // @formatter:on
  }
}
