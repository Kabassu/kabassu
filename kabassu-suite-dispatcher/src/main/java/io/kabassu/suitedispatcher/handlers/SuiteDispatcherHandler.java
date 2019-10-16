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

package io.kabassu.suitedispatcher.handlers;

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
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class SuiteDispatcherHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteDispatcherHandler.class);

  private final Vertx vertx;

  public SuiteDispatcherHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    event.reply(new JsonObject().put("status", "in progress"));
    JsonObject testSuiteRequest = event.body();
    List<Promise<JsonObject>> createdRequestsPromises = new ArrayList<>();
    testSuiteRequest.getJsonArray("definitionsData")
      .forEach(definitionData -> {
        JsonObject testRequest = mapTestRequest((JsonObject) definitionData,
          testSuiteRequest.getString(JsonFields.SUITE_ID));
        createdRequestsPromises.add(createRequest(testRequest));
      });
    List<Future> futures = createdRequestsPromises.stream().map(Promise::future)
      .collect(Collectors.toList());
    CompositeFuture.all(futures)
      .setHandler(completedFutures -> {
        if (completedFutures.succeeded()) {
          createTestSuiteRun(futures, testSuiteRequest.getString(JsonFields.SUITE_ID), testSuiteRequest.getString("viewId"));
          runTests(futures);
        }
      });

  }

  private void runTests(List<Future> futures) {
    JsonArray testRequests = new JsonArray();
    futures.forEach(future -> testRequests.add((JsonObject) future.result()));
    vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
      new JsonObject()
        .put(MessagesFields.TESTS_TO_RUN, testRequests));
  }

  private void createTestSuiteRun(List<Future> futures, String suiteId, String viewId) {
    JsonObject suiteRunRequest = new JsonObject();
    JsonArray testRequestsId = new JsonArray();
    futures.forEach(future -> testRequestsId.add(((JsonObject) future.result()).getString("_id")));
    suiteRunRequest.put(JsonFields.SUITE_ID, suiteId).put("requests", testRequestsId)
      .put("history", new JsonArray().add(new JsonObject().put("date", new Date().getTime()).put("event","Suite run created and started")));
    vertx.eventBus()
      .rxRequest("kabassu.database.mongo.addsuiterun", suiteRunRequest).toObservable()
      .doOnNext(
        eventResponse -> {
          if(StringUtils.isNotBlank(viewId)){
            updateView(viewId,((JsonObject) eventResponse.body()).getString("id"));
          }
        }).subscribe();

  }

  private JsonObject mapTestRequest(JsonObject testRequestData, String suiteId) {
    return new JsonObject()
      .put("definitionId", testRequestData.getString("definitionId"))
      .put(JsonFields.CONFIGURATION_ID, testRequestData.getString(JsonFields.CONFIGURATION_ID))
      .put(
        JsonFields.ADDITIONAL_PARAMETERS, testRequestData.getJsonObject(JsonFields.ADDITIONAL_PARAMETERS, new JsonObject()))
      .put(JsonFields.SUITE_ID, suiteId)
      .put("description", "Created for suite configuration: " + suiteId)
      .put("status", "started")
      .put("history", new JsonArray().add(new JsonObject().put("date", new Date().getTime())
        .put("event", "Request created with test suite and started")));
  }

  private void updateView(String viewId, String id) {
    vertx.eventBus().send("kabassu.database.mongo.updatearray",
      new JsonObject().put("id",viewId).put("collection","kabassu-views").put("field","suiteRunId").put("value",id).put("operation","add"));
  }

  private Promise<JsonObject> createRequest(JsonObject testRequest) {
    Promise<JsonObject> promise = Promise.promise();

    vertx.eventBus()
      .request("kabassu.database.mongo.addrequest", testRequest,
        eventResponse -> {
          if (eventResponse.succeeded()) {
            promise.complete(
              testRequest.put("_id", ((JsonObject) eventResponse.result().body()).getString("id")));
          } else {
            promise
              .complete(new JsonObject());
          }
        }
      );
    try {
      return promise;
    } catch (Exception e) {
      LOGGER.error("Error during creating  test request.", e);
      promise.complete(new JsonObject());
      return promise;
    }
  }
}
