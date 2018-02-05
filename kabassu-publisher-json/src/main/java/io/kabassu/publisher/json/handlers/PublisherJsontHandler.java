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

package io.kabassu.publisher.json.handlers;

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

public class PublisherJsontHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PublisherJsontHandler.class);

  private final Vertx vertx;

  public PublisherJsontHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject testResults = event.body();
    try {
      FileUtils.forceMkdir(new File("reports-json"));
      FileUtils.writeStringToFile(new File("reports-json/" + testResults.getString("taskId")),
          testResults.encodePrettily(),
          Charset.defaultCharset());
    } catch (IOException e) {
      LOGGER.error("Problem with creating report", e);
    }
  }

}
