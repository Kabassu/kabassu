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

package io.kabassu.results.server;

import io.kabassu.results.server.configuration.KabassuResultsServerConfiguration;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;

public class KabassuResultsServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(KabassuResultsServerVerticle.class);

  private KabassuResultsServerConfiguration options;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    options = new KabassuResultsServerConfiguration(config());
  }

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    HttpServer server = vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyStoreOptions(
      new JksOptions().setPath(options.getCertificatePath()).setPassword(options.getPassword())));
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create("*"));
    router.route("/kabassu/results/*")
        .handler(StaticHandler.create(options.getResultsDir()).setCachingEnabled(false));
    server.requestHandler(router).rxListen(options.getPort()).subscribe(
        ok -> {
          LOGGER.info("Kabassu Results Server has started. Listening on port {}",
              options.getPort());
          startFuture.complete();
        },
        error -> {
          LOGGER.error("Unable to start Kabassu Main Server.", error.getCause());
          startFuture.fail(error);
        }
    );
  }

}
