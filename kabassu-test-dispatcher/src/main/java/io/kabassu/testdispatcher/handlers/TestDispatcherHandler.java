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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.UUID;

public class TestDispatcherHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  public TestDispatcherHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    vertx.eventBus().rxSend(EventBusAdresses.KABASSU_TEST_RETRIEVER, event.body()).toObservable()
        .doOnNext(retrieverMessage -> handleRetriverMessage(retrieverMessage, event)).subscribe();
  }

  private void handleRetriverMessage(Message<Object> retrieverMessage, Message<JsonObject> event) {
    if (TestRetrieverCommands.RETURN_AVAILABLE_TESTS
        .equals(event.body().getString(MessagesFields.REQUEST))) {
      event.reply(retrieverMessage.body());
    }
    if (TestRetrieverCommands.RUN_TESTS.equals(event.body().getString(MessagesFields.REQUEST))) {

      JsonObject reply = validateAndRun(
          ((JsonObject) retrieverMessage.body()).getJsonArray(MessagesFields.REPLY), event);
      event.reply(new JsonObject().put(MessagesFields.REPLY, reply));
    } else {
      event.reply(new JsonObject().put(MessagesFields.REPLY,
          new JsonObject().put("wrong_command", event.body().getString(MessagesFields.REQUEST))));
    }
  }

  private JsonObject validateAndRun(JsonArray testsToRun,
      Message<JsonObject> event) {
    JsonObject reply = new JsonObject();
    if (testsToRun.size() != event.body().getJsonArray(MessagesFields.TESTS_TO_RUN).size()) {
      reply.put("description", "There are missing tests");
    } else {
      JsonObject sendToContext = new JsonObject();
      sendToContext.put(MessagesFields.TESTS_TO_RUN, testsToRun);
      String testRequestId = UUID.randomUUID().toString();
      sendToContext.put(MessagesFields.TEST_RUN_ID, testRequestId);
      vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
          sendToContext);
      reply.put("description", "Running tests");
      reply.put("request_id:", testRequestId);
    }
    return reply;
  }


}
