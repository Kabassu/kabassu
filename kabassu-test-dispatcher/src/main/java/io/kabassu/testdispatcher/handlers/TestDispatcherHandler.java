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
import io.kabassu.commons.constants.TestRetrieverCommands;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class TestDispatcherHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  private Message<JsonObject> event;

  public TestDispatcherHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    this.event = event;
    vertx.eventBus().rxSend(EventBusAdresses.KABASSU_TEST_RETRIEVER, event.body()).toObservable()
        .doOnNext(this::handleRetriverMessage).subscribe();
  }

  private void handleRetriverMessage(Message<Object> retrieverMessage) {
    if (TestRetrieverCommands.RETURN_AVAILABLE_TESTS
        .equals(event.body().getString(MessagesFields.REQUEST))) {
      event.reply(retrieverMessage.body());
    }
    if (TestRetrieverCommands.RUN_TESTS.equals(event.body().getString(MessagesFields.REQUEST))) {
      JsonObject testsToRun = new JsonObject();
      testsToRun.put(MessagesFields.TESTS_TO_RUN,((JsonObject) retrieverMessage.body()).getJsonArray(MessagesFields.REPLY));
      vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,testsToRun);
      event.reply(new JsonObject().put(MessagesFields.REPLY,"Running tests"));
    } else {
      event.reply(new JsonObject());
    }
  }


}
