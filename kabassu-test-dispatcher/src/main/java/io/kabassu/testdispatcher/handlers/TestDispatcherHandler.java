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

package io.kabassu.testdispatcher.handlers;

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.commons.constants.MessagesFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class TestDispatcherHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  public TestDispatcherHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject testRequest = event.body();
    //TODO: MANY TESTS IN ONE REQUEST
    vertx.eventBus().rxRequest("kabassu.database.mongo.addrequest", event.body()).toObservable()
      .doOnNext(
        eventResponse -> {
          event.reply(eventResponse.body());
          JsonObject testsToRun = new JsonObject().put(MessagesFields.TESTS_TO_RUN, new JsonArray()
            .add(new JsonObject().put(eventResponse.body().toString(), testRequest)));
          vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT, testsToRun);
        }
      ).subscribe();

    //vertx.eventBus().rxRequest("kabassu.database.mongo.getdefinition",testRequest.getString("definitionId")).toObservable()
    //  .doOnNext(
    //  eventResponse->{
    //    JsonObject definitionData = (JsonObject) eventResponse.body();
    //    if(definitionData.containsKey("_id")){
    //     runTest(event, definitionData);
    //    } else {
    //      event.reply(eventResponse.body());
    //    }
    //  }
    //).subscribe();

  }
}
