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

package io.kabassu.results.dispatcher.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Map;

public class ResultDispatchertHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  private Map<String, String> publishersMap;

  public ResultDispatchertHandler(Vertx vertx,
      Map<String, String> runnersMap) {
    this.publishersMap = runnersMap;
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    publishersMap.values().stream()
        .forEach(address -> vertx.eventBus().send(address, event.body()));
  }

}
