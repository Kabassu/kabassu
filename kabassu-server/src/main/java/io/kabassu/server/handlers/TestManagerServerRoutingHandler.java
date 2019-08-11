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

package io.kabassu.server.handlers;

import io.kabassu.commons.constants.MessagesFields;
import io.kabassu.commons.constants.TestManagerCommands;
import io.kabassu.commons.dataobjects.TestUploadFile;
import io.kabassu.commons.dataobjects.TestUploadFileBuilder;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.FileUpload;
import io.vertx.reactivex.ext.web.RoutingContext;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class TestManagerServerRoutingHandler implements Handler<RoutingContext> {

  private Vertx vertx;

  private String address;

  public TestManagerServerRoutingHandler(Vertx vertx, String address) {
    this.vertx = vertx;
    this.address = address;
  }

  @Override
  public void handle(RoutingContext routingContext) {

    if (routingContext.getBodyAsJson() == null) {
      routingContext.request().response().setStatusCode(400).end();
    } else {
      handleRequestBody(routingContext);
    }
  }

  private void handleRequestBody(RoutingContext routingContext) {
    String command = routingContext.getBodyAsJson().getString(MessagesFields.REQUEST);
    JsonArray testsData = routingContext.getBodyAsJson().getJsonArray(TestManagerCommands.TESTS_DATA);
    if (StringUtils.isNoneEmpty(command) || testsData == null || testsData.size() == 0) {
      JsonObject message = new JsonObject();
      message.put(MessagesFields.REQUEST, command);
      message.put(TestManagerCommands.TESTS_DATA, testsData);
      if (!routingContext.fileUploads().isEmpty()) {
        prepareUploadFilesData(message, routingContext.fileUploads());
      }
      vertx.eventBus().rxSend(address, message).toObservable().doOnNext(response ->
          routingContext.request().response().end(((JsonObject) response.body()).encodePrettily())
      ).subscribe();
    } else {
      routingContext.request().response().setStatusCode(400).end();
    }
  }

  private void prepareUploadFilesData(JsonObject message, Set<FileUpload> fileUploads) {
    JsonArray files = new JsonArray();
    fileUploads.stream().forEach(fileUpload ->
        files.add(buildTestFileData(fileUpload))
    );
    message.put(TestManagerCommands.TESTS_FILES, files);
  }

  private JsonObject buildTestFileData(FileUpload fileUpload) {
    TestUploadFile testUploadFile = new TestUploadFileBuilder()
        .setCharSet(fileUpload.charSet())
        .setContentTransferEncoding(fileUpload.contentTransferEncoding())
        .setContentType(fileUpload.contentType())
        .setFileName(fileUpload.fileName())
        .setName(fileUpload.name())
        .setSize(fileUpload.size())
        .setUploadedFileName(fileUpload.uploadedFileName()).createTestUploadFile();
    return JsonObject.mapFrom(testUploadFile);
  }
}
