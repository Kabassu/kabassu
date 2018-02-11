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

package io.kabassu.storage.memory.handlers;

import io.kabassu.commons.constants.MessagesFields;
import io.kabassu.commons.constants.TestRetrieverCommands;
import io.kabassu.storage.memory.data.MemoryStorage;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemoryStorageHandler implements Handler<Message<JsonObject>> {

  private Vertx vertx;

  private MemoryStorage memoryStorage;

  public MemoryStorageHandler(Vertx vertx, MemoryStorage memoryStorage) {
    this.vertx = vertx;
    this.memoryStorage = memoryStorage;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject reply = new JsonObject();
    String request = event.body()
        .getString(MessagesFields.REQUEST, TestRetrieverCommands.RETURN_AVAILABLE_TESTS);

    if (request.equals(TestRetrieverCommands.RETURN_AVAILABLE_TESTS)) {
      reply.put(MessagesFields.REPLY, new JsonArray(memoryStorage.getAllTests().stream()
          .map(JsonObject::mapFrom).collect(
              Collectors.toList())));
    } else if (request.equals(TestRetrieverCommands.RUN_TESTS)) {
      JsonArray requiredTestsInfo = new JsonArray();
      JsonArray testsToRun = event.body().getJsonArray(MessagesFields.TESTS_TO_RUN);
      testsToRun.stream().map(test -> memoryStorage.getTestByIdAsJson(test.toString()))
          .filter(Objects::nonNull).forEach(requiredTestsInfo::add);
      reply.put(MessagesFields.REPLY, requiredTestsInfo);
    } else {
      reply.put(MessagesFields.REPLY, "");
    }
    event.reply(reply);
  }

}
