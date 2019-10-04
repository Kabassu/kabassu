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

package io.kabassu.runner.command.handlers;

import io.kabassu.commons.checks.FilesDownloadChecker;
import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.CommandLines;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.runner.command.configuration.KabassuRunnerCommandConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.commons.lang3.SystemUtils;

public class RunnerCommandHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerCommandHandler.class);

  private Vertx vertx;

  private KabassuRunnerCommandConfiguration configuration;

  public RunnerCommandHandler(Vertx vertx, KabassuRunnerCommandConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
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
    String testResult = "Success";
    JsonObject testDefinition = fullRequest.getJsonObject(JsonFields.DEFINITION);
    if (ConfigurationRetriever.containsParameter(testDefinition, "runnerOptions")) {
      String runnerOptions = ConfigurationRetriever
        .getParameter(testDefinition, "runnerOptions");

      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder
        .directory(new File(ConfigurationRetriever.getParameter(testDefinition, "location")));
      if (SystemUtils.IS_OS_WINDOWS) {
        processBuilder.command(CommandLines.CMD, "/c", runnerOptions);
      } else {
        processBuilder.command(CommandLines.BASH, "-c", runnerOptions);
      }
      try {
        Process process = processBuilder.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        if((line = in.readLine()) != null) {
          testResult = "Failure";
        }

        process.waitFor();
      } catch (IOException ex) {
        LOGGER.error(ex);
        testResult = "Failure";
      } catch (InterruptedException ex) {
        LOGGER.error(ex);
        Thread.currentThread().interrupt();
      }
    }

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
