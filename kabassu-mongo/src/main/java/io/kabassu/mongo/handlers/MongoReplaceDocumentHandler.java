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

package io.kabassu.mongo.handlers;

import io.kabassu.commons.constants.JsonFields;
import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class MongoReplaceDocumentHandler extends AbstractMongoHandler<JsonObject> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoReplaceDocumentHandler.class);

  public MongoReplaceDocumentHandler(Vertx vertx, KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject request = event.body();
    request.getJsonObject("new").remove("_id");
    client.findOneAndReplace(request.getString(JsonFields.COLLECTION), new JsonObject().put("_id", request.getString("id")), request.getJsonObject("new"), res -> {
      if (res.succeeded()) {
        validateResult(event, res);
      } else {
        event.reply(new JsonObject().put("error", res.cause().getMessage()));
        LOGGER.error("Problem during replacing data", res.cause());
      }
    });
  }

  private void validateResult(Message<JsonObject> event, AsyncResult<JsonObject> res) {
    if (res.result() != null) {
      event.reply(res.result());
    } else {
      event.reply(new JsonObject().put("error", "Data not found"));
    }
  }

}
