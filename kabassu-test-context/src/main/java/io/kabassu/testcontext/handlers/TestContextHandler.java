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

package io.kabassu.testcontext.handlers;

import io.kabassu.commons.constants.MessagesFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class TestContextHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestContextHandler.class);
  private static final String RUNNER = "runner";

  private final Vertx vertx;

  private Map<String, String> runnersMap;

  public TestContextHandler(Vertx vertx,
    Map<String, String> runnersMap) {
    this.runnersMap = runnersMap;
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    event.body().getJsonArray(MessagesFields.TESTS_TO_RUN)
      .stream()
      .forEach(
        testRequest -> {
          Promise<JsonObject> mergeWithDefinitionPromise = mergeWithDefinition(
            (JsonObject) testRequest);
          Promise<JsonObject> mergeWithConfigurationPromise = mergeRequestWithConfiguration(
            (JsonObject) testRequest);
          CompositeFuture
            .all(mergeWithDefinitionPromise.future(), mergeWithConfigurationPromise.future())
            .setHandler(
              completedFutures -> {
                if (completedFutures.succeeded() && existingRunner(
                  mergeWithDefinitionPromise.future().result())) {
                  JsonObject completeTestResults = new JsonObject();
                  completeTestResults.put("testRequest",
                    (JsonObject) mergeWithConfigurationPromise.future().result());
                  completeTestResults
                    .put("definition", mergeWithDefinitionPromise.future().result());
                  callRunner(completeTestResults);
                }
              }
            );
        }
      );
  }

  private boolean existingRunner(JsonObject result) {
    return runnersMap.containsKey(result.getString(RUNNER));
  }

  private Promise<JsonObject> mergeRequestWithConfiguration(JsonObject testRequest) {
    Promise<JsonObject> promise = Promise.promise();
    if (testRequest.containsKey("configurationId") && StringUtils
      .isNotBlank(testRequest.getString("configurationId"))) {
      addConfiguration(promise, testRequest);
    } else {
      promise.complete(testRequest);
    }
    try {
      return promise;
    } catch (Exception e) {
      LOGGER.error("Error during recovering configuration. configuration id {}",
        testRequest.getString("configuration"), e);
      promise.complete(new JsonObject().put(RUNNER, ""));
      return promise;
    }
  }

  private Promise<JsonObject> mergeWithDefinition(JsonObject testRequest) {
    Promise<JsonObject> promise = Promise.promise();
    vertx.eventBus()
      .request("kabassu.database.mongo.getdefinition", testRequest.getString("definitionId"),
        eventResponse -> {
          if (eventResponse.succeeded()) {
            JsonObject definitionData = (JsonObject) eventResponse.result().body();
            if (definitionData != null && definitionData.containsKey("_id")) {

              if (definitionData.containsKey("configurationId") && StringUtils
                .isNotBlank(definitionData.getString("configurationId"))) {
                addConfiguration(promise, definitionData);
              } else {
                promise.complete(definitionData);
              }
            } else {
              promise
                .complete(new JsonObject().put(RUNNER, ""));
            }
          } else {
            promise
              .complete(new JsonObject().put(RUNNER, ""));
          }
        }
      );
    try {
      return promise;
    } catch (Exception e) {
      LOGGER.error("Error during recovering test definition. Definition id {}",
        testRequest.getString("definitionId"), e);
      promise.complete(new JsonObject().put(RUNNER, ""));
      return promise;
    }
  }

  private void addConfiguration(Promise<JsonObject> promise,
    JsonObject jsonWithAdditionalParameters) {
    vertx.eventBus()
      .request("kabassu.database.mongo.getconfiguration",
        jsonWithAdditionalParameters.getString("configurationId"),
        eventResponse -> {
          if (eventResponse.succeeded()) {
            JsonObject configurationData = (JsonObject) eventResponse.result().body();
            if (configurationData != null && configurationData.containsKey("_id")) {
              promise.complete(
                jsonWithAdditionalParameters.put("configurationParameters", configurationData.getJsonObject("parameters")));
            } else {
              promise
                .complete(jsonWithAdditionalParameters);
            }
          } else {
            promise
              .complete(jsonWithAdditionalParameters);
          }
        }
      );
  }

  private void callRunner(JsonObject completeTestRequest) {
    vertx.eventBus()
      .send(runnersMap.get(completeTestRequest.getJsonObject("definition").getString(RUNNER)),
        completeTestRequest);
  }

}
