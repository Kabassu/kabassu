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
import io.kabassu.server.configuration.RoutingPath;
import io.kabassu.server.handlers.DefaultServerRoutingHandler;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public class KabassuServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KabassuServerVerticle.class);

  private KabassuServerConfiguration kabassuServerConfiguration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    kabassuServerConfiguration = new KabassuServerConfiguration(config());
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Router router = Router.router(vertx);
    for (RoutingPath routingPath : kabassuServerConfiguration.getRoutingPath()) {
      router.route(routingPath.getMethod(), routingPath.getPath())
          .handler(createHandler(routingPath.getHandler(), routingPath.getAddress()));
    }

    vertx.createHttpServer().requestHandler(router::accept)
        .rxListen(kabassuServerConfiguration.getPort()).subscribe(
        ok -> {
          LOGGER.info("Kabassu Main Server has started. Listening on port {}",
              kabassuServerConfiguration.getPort());
          startFuture.complete();
        },
        error -> {
          LOGGER.error("Unable to start Kabassu Main Server.", error.getCause());
          startFuture.fail(error);
        }
    );
  }

  private Handler<RoutingContext> createHandler(String handler, String address) {
    if (StringUtils.isEmpty(handler) || handler.equalsIgnoreCase("default")) {
      return new DefaultServerRoutingHandler(vertx, address);
    } else {
      throw new IllegalArgumentException("Other handler are not supported yet");
    }
  }

}
