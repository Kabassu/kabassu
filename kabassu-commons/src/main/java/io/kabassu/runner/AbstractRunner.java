package io.kabassu.runner;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.CommandLines;
import io.kabassu.commons.constants.JsonFields;
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

public abstract class AbstractRunner implements Handler<Message<JsonObject>> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRunner.class);

  protected final Vertx vertx;

  public AbstractRunner(Vertx vertx) {
    this.vertx = vertx;
  }

  protected abstract void runTest(JsonObject fullRequest);

  public void handle(Message<JsonObject> event) {
    runTestWithFilesValidation(event);
  }

  protected void runTestWithFilesValidation(Message<JsonObject> event) {
    vertx.eventBus().rxRequest("kabassu.filesretriever",
      event.body()).toObservable()
      .doOnNext(
        eventResponse ->
          runTest((JsonObject) eventResponse.body())
      ).subscribe();
  }

  protected String runCommand(String command, JsonObject testDefinition) {
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

  protected JsonObject updateHistory(JsonObject testRequest, String testResult) {
    testRequest.getJsonArray("history").add(new JsonObject().put("date", new Date().getTime())
      .put("event", "Test finished with: " + testResult));
    return new JsonObject().put("new", testRequest)
      .put(JsonFields.COLLECTION, "kabassu-requests").put("id", testRequest.getString("_id"));
  }

  protected void finishRun(JsonObject fullRequest, String testResult) {
    JsonObject updateHistory = updateHistory(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
      testResult);
    vertx.eventBus().rxRequest("kabassu.database.mongo.replacedocument", updateHistory)
      .toObservable()
      .doOnNext(
        eventResponse ->
          vertx.eventBus().send("kabassu.results.dispatcher",
            fullRequest.put("result", testResult)
              .put(JsonFields.TEST_REQUEST, updateHistory.getJsonObject("new")))

      ).subscribe();
  }
}
