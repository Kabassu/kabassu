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

package io.kabassu.runner.handlers;

import io.kabassu.commons.checks.FilesDownloadChecker;
import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.multipart.MultipartForm;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import org.apache.commons.io.FileUtils;

public class RunnerAETHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerAETHandler.class);
  public static final String SUITE = "suite";

  private Vertx vertx;


  public RunnerAETHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {

    if (FilesDownloadChecker
      .checkIfFilesRetrieveIsRequired(
        event.body().getJsonObject(JsonFields.DEFINITION).getString("locationType"))) {
      vertx.eventBus().rxRequest("kabassu.filesretriever",
        event.body()).toObservable()
        .doOnNext(
          eventResponse ->
            runTest((JsonObject) eventResponse.body())
        ).subscribe();
    } else {
      runTest(event.body());
    }
  }

  private void runTest(JsonObject fullRequest) {
    boolean rerun = fullRequest.containsKey("rerun");
    fullRequest.remove("rerun");

    String testResult = "FAILURE";
    JsonObject testDefinition = fullRequest.getJsonObject(JsonFields.DEFINITION);

    //definition:server,suite
    //request: domain, pattern, name

    if (ConfigurationRetriever.containsParameter(testDefinition, "server") && ConfigurationRetriever
      .containsParameter(testDefinition, SUITE)) {
      testResult = "SUCCESS";
      String server = ConfigurationRetriever
        .getParameter(testDefinition, "server");
      String suite = ConfigurationRetriever
        .getParameter(testDefinition, SUITE);
      String port = "8181";
      if(ConfigurationRetriever.containsParameter(testDefinition, "port")){
        port = ConfigurationRetriever
          .getParameter(testDefinition, "port");
      }

      if(rerun && fullRequest.getJsonObject(JsonFields.TEST_REQUEST).containsKey("correlationId")){
        //TODO RERUN
      } else {
        try {
          String suiteXML = FileUtils
            .readFileToString(new File(testDefinition.getString("location"), suite),
              Charset.defaultCharset());

          MultipartForm form = MultipartForm.create()
            .attribute(SUITE, suiteXML);
          WebClient.create(vertx).post(Integer.valueOf(port), server,"/suite")
            .sendMultipartForm(form, ar -> {
              if (ar.succeeded()) {
                // Ok
              }
            });

        } catch (IOException e) {
          LOGGER.error(
            "Error while reading suite file: " + testDefinition.getString("location") + "/" + suite);
          testResult = "FAILURE";
          finishRun(fullRequest, testResult);
        }
      }

    } else {
      finishRun(fullRequest, testResult);
    }

  }

  private void finishRun(JsonObject fullRequest, String testResult) {
    final String result = testResult;
    JsonObject updateHistory = updateHistory(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
      testResult);
    vertx.eventBus().rxRequest("kabassu.database.mongo.replacedocument", updateHistory)
      .toObservable()
      .doOnNext(
        eventResponse ->
          vertx.eventBus().send("kabassu.results.dispatcher",
            fullRequest.put("result", result)
              .put(JsonFields.TEST_REQUEST, updateHistory.getJsonObject("new")))

      ).subscribe();
  }


  private JsonObject updateHistory(JsonObject testRequest, String testResult) {
    testRequest.getJsonArray("history").add(new JsonObject().put("date", new Date().getTime())
      .put("event", "Test finished with: " + testResult));
    return new JsonObject().put("new", testRequest)
      .put(JsonFields.COLLECTION, "kabassu-requests").put("id", testRequest.getString("_id"));
  }

}
