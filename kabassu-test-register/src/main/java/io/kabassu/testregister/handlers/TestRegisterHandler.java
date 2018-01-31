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

package io.kabassu.testregister.handlers;

import io.kabassu.commons.constants.MessagesFields;
import io.kabassu.commons.constants.TestRetrieverCommands;
import io.kabassu.mocks.TestClassesMocks;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class TestRegisterHandler implements Handler<Message<JsonObject>> {

  private Vertx vertx;

  public TestRegisterHandler(Vertx vertx) {
    this.vertx = vertx;
  }


  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject messageBody = event.body();
    String request = messageBody
        .getString(MessagesFields.REQUEST, TestRetrieverCommands.RETURN_AVAILABLE_TESTS);
    parseRequest(request, event);
  }

  private void parseRequest(String request, Message<JsonObject> event) {
    JsonObject reply = new JsonObject();
    if (request.equals(TestRetrieverCommands.RETURN_AVAILABLE_TESTS)) {
      reply.put(MessagesFields.REPLY, TestClassesMocks.getExistingTests());
    } else {
      reply.put(MessagesFields.REPLY, "");
    }
    event.reply(reply);
  }
}
