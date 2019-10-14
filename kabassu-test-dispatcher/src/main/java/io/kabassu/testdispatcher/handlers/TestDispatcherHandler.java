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

package io.kabassu.testdispatcher.handlers;

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.commons.constants.MessagesFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Date;

public class TestDispatcherHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  public TestDispatcherHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject requestWithStatus = addStatusAndHistory(event.body());
    vertx.eventBus().rxRequest("kabassu.database.mongo.addrequest",
      requestWithStatus).toObservable()
      .doOnNext(
        eventResponse -> {
          event.reply(eventResponse.body());
          if(requestWithStatus.containsKey("viewId")){
            updateView(requestWithStatus.getString("viewId"),((JsonObject) eventResponse.body()).getString("id"));
          }
          vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
            new JsonObject()
              .put(MessagesFields.TESTS_TO_RUN,
                new JsonArray()
                  .add(requestWithStatus
                    .put("_id", ((JsonObject) eventResponse.body()).getString("id")))));
        }
      ).subscribe();
  }

  private void updateView(String id, String viewId) {
    vertx.eventBus().send("kabassu.database.mongo.updatearray",
      new JsonObject().put("id",viewId).put("collection","kabassu-views").put("field","executionId").put("value",id));
  }

  private JsonObject addStatusAndHistory(JsonObject request) {
    return request.put("status", "started")
      .put("history", new JsonArray().add(new JsonObject().put("date", new Date().getTime()).put("event","Request created and started")));
  }
}
