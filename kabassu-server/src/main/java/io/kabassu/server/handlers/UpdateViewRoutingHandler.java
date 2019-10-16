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

package io.kabassu.server.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public class UpdateViewRoutingHandler implements Handler<RoutingContext> {

  private Vertx vertx;

  private String address;

  public UpdateViewRoutingHandler(Vertx vertx, String address) {
    this.vertx = vertx;
    this.address = address;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String requestString = routingContext.getBodyAsString();
    if (StringUtils.isNotEmpty(requestString)) {
      addData(requestString,routingContext.request().response());
    } else {
      routingContext.request().response().putHeader("content-type", "application/json")
          .end(new JsonObject().put("response", "No data to add").encodePrettily());
    }

  }

  private void addData(String requestString, HttpServerResponse response) {
    JsonObject request = new JsonObject(requestString).put("collection","kabassu-views");
    vertx.eventBus().rxRequest(address, request).toObservable().doOnNext(eventResponse ->
        response.end(((JsonObject) eventResponse.body()).encodePrettily())
    ).subscribe();
  }
}
