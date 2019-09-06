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

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.SingleSource;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;

class HttpServerProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerProvider.class);
  private final Vertx vertx;
  private final int port;

  public HttpServerProvider(Vertx vertx, int port) {
    this.vertx = vertx;
    this.port = port;
  }

  public SingleSource<HttpServer> configureHttpServer(Router router) {
    HttpServer httpServer = vertx.createHttpServer();
    httpServer
        .requestHandler(req -> routeSafe(req, router));
    return httpServer.rxListen(port);
  }

  private void routeSafe(HttpServerRequest req, Router router) {
    try {
      router.handle(req);
    } catch (IllegalArgumentException ex) {
      LOGGER.warn("Problem decoding Query String ", ex);

      req.response()
          .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
          .setStatusMessage(HttpResponseStatus.BAD_REQUEST.reasonPhrase())
          .end("Invalid characters in Query Parameter");
    }
  }

}
