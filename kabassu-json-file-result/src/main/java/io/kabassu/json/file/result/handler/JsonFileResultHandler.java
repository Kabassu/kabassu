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

package io.kabassu.json.file.result.handler;

import io.kabassu.commons.constants.MessagesFields;
import io.kabassu.json.file.result.configuration.KabassuJsonFileResultConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;

public class JsonFileResultHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileResultHandler.class);

  private final Vertx vertx;

  private final String directory;

  public JsonFileResultHandler(Vertx vertx,
      KabassuJsonFileResultConfiguration configuration) {
    this.vertx = vertx;
    this.directory = configuration.getDirectory();
  }

  @Override
  public void handle(Message<JsonObject> event) {
    String resultsId = event.body().getString(MessagesFields.TEST_RUN_ID);
    JsonObject reply = new JsonObject();
    try {
      File resultFile = new File(directory, resultsId + ".json");
      if (resultFile.exists()) {
        String result = FileUtils.readFileToString(resultFile, Charset.defaultCharset());
        reply.put(MessagesFields.REPLY, new JsonObject(result));
      } else {
        reply.put(MessagesFields.REPLY, "Wrong result id: " + resultsId);
      }
    } catch (IOException e) {
      LOGGER.error("Problem with creating report", e);
      reply.put(MessagesFields.REPLY, "Error while reading result file");
    }
    event.reply(reply);
  }

}
