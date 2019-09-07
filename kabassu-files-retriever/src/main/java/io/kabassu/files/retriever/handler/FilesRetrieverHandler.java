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

package io.kabassu.files.retriever.handler;

import io.kabassu.files.retriever.configuration.KabassuFilesRetrieverConfiguration;
import io.kabassu.files.retriever.retrievers.AbstractFileRetriever;
import io.kabassu.files.retriever.retrievers.FilesRetrieversFactory;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class FilesRetrieverHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  private final String downloadDirectory;

  public FilesRetrieverHandler(Vertx vertx, KabassuFilesRetrieverConfiguration configuration) {
    this.vertx = vertx;
    this.downloadDirectory = configuration.getDownloadDirectory();
  }

  @Override
  public void handle(Message<JsonObject> event) {
    AbstractFileRetriever fileRetriever = new FilesRetrieversFactory(downloadDirectory)
      .getFileRetriever(event.body());
    JsonObject updateRequest = fileRetriever.getFiles();
    event.reply(updateRequest);
  }
}
