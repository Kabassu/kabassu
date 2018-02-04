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

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.commons.constants.MessagesFields;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Map;

public class TestContextHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestContextHandler.class);

  private final Vertx vertx;

  private Map<String, String> runnersMap;

  public TestContextHandler(Vertx vertx,
      Map<String, String> runnersMap) {
    this.runnersMap = runnersMap;
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject testResults = new JsonObject();
    Observable.fromIterable((event.body().getJsonArray(MessagesFields.TESTS_TO_RUN)))
        .filter(testInfoObject ->
            runnersMap.containsKey(((JsonObject) testInfoObject).getString("runner"))
        )
        .flatMap(testInfoObject -> callRunner((JsonObject) testInfoObject).toObservable())
        .map(results -> mergeResults(testResults, results))
        .doOnComplete(() -> vertx.eventBus().send(EventBusAdresses.KABASSU_RESULTS_DISPATCHER,testResults))
        .subscribe();
  }

  private Single<Message<Object>> callRunner(JsonObject message) {
    return vertx.eventBus()
        .rxSend(runnersMap.get(message.getString("runner")), message.getString("testClass"));
  }

  private JsonObject mergeResults(JsonObject testResults, Message<Object> results) {
    return testResults.put(((JsonObject) results.body()).getString("resultsId"), results.body());
  }

}
