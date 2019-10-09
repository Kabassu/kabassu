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

package io.kabassu.test.rerun.handlers;

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.commons.constants.MessagesFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Date;

public class TestRerunHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  public TestRerunHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    vertx.eventBus()
      .rxRequest("kabassu.database.mongo.getrequest", event.body().getString("requestId"))
      .toObservable()
      .doOnNext(
        eventResponse -> {
          JsonObject updateHistory = updateHistory((JsonObject) eventResponse.body());
          vertx.eventBus().rxRequest("kabassu.database.mongo.replacedocument", updateHistory).toObservable()
            .doOnNext(updateResponse->{
              if (updateHistory.getJsonObject("new").containsKey("_id")) {
                event.reply(updateHistory.getJsonObject("new"));
                vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
                  new JsonObject()
                    .put(MessagesFields.TESTS_TO_RUN,
                      new JsonArray()
                        .add(updateHistory.getJsonObject("new"))));
              }
              }
            ).subscribe();
        }
      ).subscribe();
  }

  private JsonObject updateHistory(JsonObject testRequest) {
    testRequest.put("status","started");
    testRequest.getJsonArray("history").add(new JsonObject().put("date",new Date().getTime()).put("event","Request rerun started"));
    return new JsonObject().put("new",testRequest)
      .put(JsonFields.COLLECTION,"kabassu-requests").put("id",testRequest.getString("_id"));
  }

}
