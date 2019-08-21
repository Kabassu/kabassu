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

package io.kabassu.mongo.handlers;

import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Date;

public class MongoAddDataHandler extends AbstractMongoHandler<JsonObject> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoAddDataHandler.class);

  private final String collection;

  public MongoAddDataHandler(Vertx vertx, KabassuMongoConfiguration configuration,
    String collection) {
    super(vertx, configuration);
    this.collection = collection;
  }

  @Override
  public void handle(Message<JsonObject> event) {

    client.insert(collection, addDate(event.body()), res -> {
      if (res.succeeded()) {
        event.reply(new JsonObject().put("id", res.result()));
      } else {
        event.reply(new JsonObject().put("error", res.cause().getMessage()));
        LOGGER.error("Problem during adding data", res.cause());
      }
    });
  }

  private JsonObject addDate(JsonObject request) {
    return request.put("created", new Date().getTime());
  }

}
