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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DefaultServerRoutingHandler implements Handler<RoutingContext> {

  private Vertx vertx;

  private String address;

  public DefaultServerRoutingHandler(Vertx vertx, String address) {
    this.vertx = vertx;
    this.address = address;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String requestedTests = routingContext.request().getParam("tests");
    if (StringUtils.isNotEmpty(requestedTests)) {
      List<String> testsToRun = new ArrayList<>(
          Arrays.asList(StringUtils.split(requestedTests, ";")));
      runTests(testsToRun, routingContext.request().response());
    } else {
      routingContext.request().response().putHeader("content-type", "application/json")
          .end(new JsonObject().put("response", "No tests to run").encodePrettily());
    }

  }

  private void runTests(List<String> testsToRun, HttpServerResponse response) {
    JsonObject message = new JsonObject();
    message.put(MessagesFields.REQUEST, TestRetrieverCommands.RUN_TESTS);
    message.put(MessagesFields.TESTS_TO_RUN, new JsonArray(testsToRun));
    vertx.eventBus().rxRequest(address, message).toObservable().doOnNext(eventResponse ->
        response.end(((JsonObject) eventResponse.body()).encodePrettily())
    ).subscribe();
  }
}
