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

package io.kabassu.runner.lighthouse.handlers;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.CommandLines;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.runner.AbstractRunner;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.SystemUtils;

public class RunnerLighthouseHandler extends AbstractRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerLighthouseHandler.class);

  public RunnerLighthouseHandler(Vertx vertx) {
    super(vertx);
  }

  protected void runTest(JsonObject fullRequest) {

    final String result = runCommand(fullRequest.getJsonObject(JsonFields.DEFINITION),
      prepareLighthouseCommand(fullRequest));
    JsonObject updateHistory = updateHistory(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
      result);
    vertx.eventBus().rxRequest("kabassu.database.mongo.replacedocument", updateHistory)
      .toObservable()
      .doOnNext(
        eventResponse ->
          vertx.eventBus().send("kabassu.results.dispatcher",
            fullRequest.put("result", result)
              .put(JsonFields.TEST_REQUEST, updateHistory.getJsonObject("new")))

      ).subscribe();

  }

  private String prepareLighthouseCommand(JsonObject fullRequest) {
    JsonObject definition = fullRequest.getJsonObject(JsonFields.DEFINITION);
    Map<String, String> allParameters = ConfigurationRetriever
      .mergeParametersToMap(definition, fullRequest.getJsonObject(JsonFields.TEST_REQUEST));
    StringBuilder command = new StringBuilder("lighthouse ");
    command.append(ConfigurationRetriever.getParameter(definition,"url"));

    allParameters.entrySet().stream().filter(parameter -> parameter.getKey().startsWith("--")).forEach(parameter ->
      command
        .append(" ")
        .append(parameter.getKey())
        .append(" ")
        .append(parameter.getValue())
    );

    return command.toString();
  }

  private String runCommand(JsonObject testDefinition, String command) {
    String testResult = "Success";
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder
      .directory(new File(ConfigurationRetriever.getParameter(testDefinition, "location")));
    if (SystemUtils.IS_OS_WINDOWS) {
      processBuilder.command(CommandLines.CMD, "/c", command);
    } else {
      processBuilder.command(CommandLines.BASH, "-c", command);
    }
    try {
      Process process = processBuilder.start();
      BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      if (in.ready()) {
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
    return testResult;
  }

  private JsonObject updateHistory(JsonObject testRequest, String testResult) {
    testRequest.getJsonArray("history").add(new JsonObject().put("date", new Date().getTime())
      .put("event", "Test finished with: " + testResult));
    return new JsonObject().put("new", testRequest)
      .put(JsonFields.COLLECTION, "kabassu-requests").put("id", testRequest.getString("_id"));
  }

}
