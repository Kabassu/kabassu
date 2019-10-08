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

package io.kabassu.suite.rerun.handlers;

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.commons.constants.MessagesFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuiteRerunHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerunHandler.class);

  private final Vertx vertx;

  public SuiteRerunHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    event.reply(new JsonObject().put("status", "in progress"));
    vertx.eventBus()
      .rxRequest("kabassu.database.mongo.getsuiterun", event.body().getString("suiterunId"))
      .toObservable()
      .doOnNext(
        eventResponse -> {
          vertx.eventBus().send("kabassu.database.mongo.replacedocument",
            updateSuiteHistory((JsonObject) eventResponse.body()));

          executeTests(((JsonObject) eventResponse.body()).getJsonArray("requests"));
        }
      ).subscribe();
  }

  private void executeTests(JsonArray requests) {
    List<Future> retrievedRequests = new ArrayList<>();
    requests.forEach(request -> retrievedRequests.add(retrieveRequest((String) request).future()));
    CompositeFuture.all(retrievedRequests).setHandler(
      completedFutures -> {
        if (completedFutures.succeeded()) {
          updateTests(retrievedRequests);
        }
      });
  }

  private void updateTests(List<Future> futures) {
    List<Future> updatedRequests = new ArrayList<>();
    futures.stream().filter(future -> ((JsonObject) future.result()).containsKey("new"))
      .forEach(request -> updatedRequests.add(updateTest((JsonObject) request.result()).future()));
    CompositeFuture.all(updatedRequests).setHandler(
      completedFutures -> {
        if (completedFutures.succeeded()) {
          runTests(updatedRequests);
        }
      });
  }

  private Promise<JsonObject> updateTest(JsonObject request) {
    return getPromiseWithRequest("kabassu.database.mongo.replacedocument", request,
      "Error during updating  test request.");
  }

  private void runTests(List<Future> futures) {
    JsonArray testRequests = new JsonArray();
    futures.stream().filter(future -> ((JsonObject) future.result()).containsKey("new"))
      .forEach(future -> testRequests
        .add((JsonObject) ((JsonObject) future.result()).getJsonObject("new")));
    vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
      new JsonObject()
        .put(MessagesFields.TESTS_TO_RERUN, testRequests));
  }

  private Promise<JsonObject> retrieveRequest(String request) {
    return getPromiseWithRequest("kabassu.database.mongo.getrequest", request,
      "Error during creating  test request.");
  }

  private Promise<JsonObject> getPromiseWithRequest(String address, Object request,
    String errorMessage) {
    Promise<JsonObject> promise = Promise.promise();

    vertx.eventBus()
      .request(address, request,
        eventResponse -> {
          if (eventResponse.succeeded()) {
            JsonObject testRequest = (JsonObject) eventResponse.result().body();
            promise.complete(updateHistory(testRequest));
          } else {
            promise
              .complete(new JsonObject());
          }
        }
      );
    try {
      return promise;
    } catch (Exception e) {
      LOGGER.error(errorMessage, e);
      promise.complete(new JsonObject());
      return promise;
    }
  }

  private JsonObject updateSuiteHistory(JsonObject suiteRun) {
    suiteRun.getJsonArray("history").add(new JsonObject().put("date", new Date().getTime())
      .put("event", "Suite Execution rerun started"));
    return new JsonObject().put("new", suiteRun)
      .put(JsonFields.COLLECTION, "kabassu-suite-runs").put("id", suiteRun.getString("_id"));
  }

  private JsonObject updateHistory(JsonObject testRequest) {
    testRequest.put("status", "started");
    testRequest.getJsonArray("history").add(
      new JsonObject().put("date", new Date().getTime())
        .put("event", "Request rerun from suite started"));
    return new JsonObject().put("new", testRequest)
      .put(JsonFields.COLLECTION, "kabassu-requests").put("id", testRequest.getString("_id"));
  }
}
