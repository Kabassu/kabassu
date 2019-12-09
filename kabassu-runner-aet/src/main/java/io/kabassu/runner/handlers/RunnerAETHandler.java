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
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import io.vertx.reactivex.ext.web.multipart.MultipartForm;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

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
    Map<String, String> allParameters = ConfigurationRetriever
      .mergeParametersToMap(fullRequest.getJsonObject(JsonFields.DEFINITION),
        fullRequest.getJsonObject(JsonFields.TEST_REQUEST));
    if (allParameters.containsKey("server") && allParameters.containsKey(SUITE)) {
      String server = allParameters.getOrDefault("server", StringUtils.EMPTY);
      String suite = allParameters.getOrDefault(SUITE, StringUtils.EMPTY);
      String port = allParameters.getOrDefault("port", "8181");

      try {
        String suiteXML = FileUtils
          .readFileToString(
            new File(allParameters.getOrDefault("location", StringUtils.EMPTY), suite),
            Charset.defaultCharset());

        MultipartForm form = MultipartForm.create();

        form.attribute(SUITE, suiteXML);
        if (allParameters.containsKey(DOMAIN)) {
          form.attribute(DOMAIN, allParameters.get(DOMAIN));
        }
        if (allParameters.containsKey(PATTERN)) {
          form.attribute(PATTERN,
            allParameters.get(PATTERN));
        }
        if (allParameters.containsKey(NAME)) {
          form.attribute(NAME,
            allParameters.get(NAME));
        }
        sendRequest(fullRequest, server, port, form, "/suite");

      } catch (IOException e) {
        LOGGER.error(
          "Error while reading suite file: " + allParameters.get("location") + "/"
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

}
