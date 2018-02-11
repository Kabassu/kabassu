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

import io.kabassu.commons.constants.MessagesFields;
import io.kabassu.commons.constants.TestRetrieverCommands;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;

public class ResultsServerRoutingHandler implements Handler<RoutingContext> {

  private Vertx vertx;

  private String address;

  public ResultsServerRoutingHandler(Vertx vertx, String address) {
    this.vertx = vertx;
    this.address = address;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    JsonObject message = new JsonObject();
    message.put(MessagesFields.RESULTS_STORAGE, routingContext.request().getParam("resultStorage"));
    message.put(MessagesFields.TEST_RUN_ID, routingContext.request().getParam("id"));

    vertx.eventBus().rxSend(address, message).toObservable().doOnNext(response ->
        routingContext.request().response().end(((JsonObject) response.body()).encodePrettily())
    ).subscribe();

  }
}
