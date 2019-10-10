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

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.runner.AbstractRunner;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import io.vertx.reactivex.ext.web.multipart.MultipartForm;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import org.apache.commons.io.FileUtils;

public class RunnerAETHandler extends AbstractRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerAETHandler.class);

  private static final String DOMAIN = "domain";
  private static final String SUITE = "suite";

  private static final String FAILURE = "FAILURE";
  private static final String SUCESSS = "SUCCESS";
  private static final String CORRELATION_ID = "correlationId";
  private static final String PATTERN = "pattern";
  private static final String NAME = "name";

  public RunnerAETHandler(Vertx vertx) {
    super(vertx);
  }

  protected void runTest(JsonObject fullRequest) {
    JsonObject testDefinition = fullRequest.getJsonObject(JsonFields.DEFINITION);
    if (ConfigurationRetriever.containsParameter(testDefinition, "server") && ConfigurationRetriever
      .containsParameter(testDefinition, SUITE)) {
      String server = ConfigurationRetriever
        .getParameter(testDefinition, "server");
      String suite = ConfigurationRetriever
        .getParameter(testDefinition, SUITE);
      String port = "8181";
      if (ConfigurationRetriever.containsParameter(testDefinition, "port")) {
        port = ConfigurationRetriever
          .getParameter(testDefinition, "port");
      }

      try {
        String suiteXML = FileUtils
          .readFileToString(
            new File(ConfigurationRetriever.getParameter(testDefinition, "location"), suite),
            Charset.defaultCharset());

        MultipartForm form = MultipartForm.create();

        form.attribute(SUITE, suiteXML);
        if (ConfigurationRetriever
          .containsParameter(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
            DOMAIN)) {
          form.attribute(DOMAIN, ConfigurationRetriever
            .getParameter(fullRequest.getJsonObject(JsonFields.TEST_REQUEST), DOMAIN));
        }
        if (ConfigurationRetriever
          .containsParameter(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
            PATTERN)) {
          form.attribute(PATTERN,
            ConfigurationRetriever.getParameter(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
              PATTERN));
        }
        if (ConfigurationRetriever
          .containsParameter(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
            NAME)) {
          form.attribute(NAME,
            ConfigurationRetriever.getParameter(fullRequest.getJsonObject(JsonFields.TEST_REQUEST),
              NAME));
        }
        sendRequest(fullRequest, server, port, form, "/suite");

      } catch (IOException e) {
        LOGGER.error(
          "Error while reading suite file: " + testDefinition.getString("location") + "/"
            + suite);
        finishRun(fullRequest, FAILURE);
      }


    } else {
      finishRun(fullRequest, FAILURE);
    }

  }

  private void sendRequest(JsonObject fullRequest, String server, String port, MultipartForm form,
    String path) {
    WebClient.create(vertx).post(Integer.valueOf(port), server, path)
      .as(BodyCodec.jsonObject())
      .sendMultipartForm(form, ar -> {
        if (ar.succeeded()) {
          JsonObject result = ar.result().body();
          if (result.containsKey(CORRELATION_ID)) {
            fullRequest.getJsonObject(JsonFields.TEST_REQUEST)
              .put(CORRELATION_ID, result.getString(CORRELATION_ID));
            fullRequest.getJsonObject(JsonFields.TEST_REQUEST).put("aetResponse", result);
            finishRun(fullRequest, SUCESSS);
          } else {
            fullRequest.getJsonObject(JsonFields.TEST_REQUEST).put("aetResponse", result);
            finishRun(fullRequest, FAILURE);
          }
        } else {
          finishRun(fullRequest, FAILURE);
        }
      });
  }

  private void finishRun(JsonObject fullRequest, String testResult) {
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


  private JsonObject updateHistory(JsonObject testRequest, String testResult) {
    testRequest.getJsonArray("history").add(new JsonObject().put("date", new Date().getTime())
      .put("event", "Test finished with: " + testResult));
    return new JsonObject().put("new", testRequest)
      .put(JsonFields.COLLECTION, "kabassu-requests").put("id", testRequest.getString("_id"));
  }

}
