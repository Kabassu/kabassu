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

package io.kabassu.results.retriever.main.handlers;

import io.kabassu.results.retriever.main.configuration.KabassuResultsRetrieverMainConfiguration;
import io.kabassu.results.retriever.main.reports.ReportsRetriever;
import io.kabassu.results.retriever.main.reports.ReportsRetrieverFactory;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KabassuResultsRetrieverMainHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory
    .getLogger(KabassuResultsRetrieverMainHandler.class);

  private Vertx vertx;

  private KabassuResultsRetrieverMainConfiguration configuration;
  private ReportsRetrieverFactory reportsRetrieverFactory;

  public KabassuResultsRetrieverMainHandler(Vertx vertx,
    KabassuResultsRetrieverMainConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
    reportsRetrieverFactory = new ReportsRetrieverFactory(configuration.getDefaultReportsDir());
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject testData = event.body();
    List<String> reports = new ArrayList<>();
    if (testData.getJsonObject("definition").containsKey("reports")) {
      reports = testData.getJsonObject("definition").getJsonArray("reports").getList();
    } else {
      reports.add(configuration.getDefaultReports());
    }
    List<JsonObject> downloadReports = new ArrayList<>();
    reports.forEach(report -> {
      ReportsRetriever reportsRetriever = reportsRetrieverFactory
        .getReportsRetriever(report, testData.getJsonObject("testRequest").getString("_id"),
          testData.getJsonObject("definition").getJsonObject("additionalParameters").getString("location"));
      try {
        downloadReports.add(
          new JsonObject().put("location", reportsRetriever.retrieveReport()).put("downloadPath",
            reportsRetriever.retrieveLink()).put("reportType",report).put("type",configuration.getReportsTypes().get(report).getString("type","multi")));
      } catch (IOException | InterruptedException e) {
        LOGGER.error("Problem with report download", e);
      }

    });
    JsonObject resultsData = new JsonObject();
    resultsData.put("testId", testData.getJsonObject("testRequest").getString("_id"));
    resultsData.put("result", testData.getString("result"));
    testData.put("downloadedReports", new JsonArray(downloadReports));
    vertx.eventBus().send("kabassu.database.mongo.addresults", testData);
    updateHistory(testData.getJsonObject("testRequest"));
  }

  private void updateHistory(JsonObject testRequest) {
    testRequest.put("status","finished");
    testRequest.getJsonArray("history").add(new JsonObject().put("date",new Date().getTime()).put("event","Reports downloaded"));
    JsonObject updateRequest = new JsonObject().put("new",testRequest)
      .put("collection","kabassu-requests").put("id",testRequest.getString("_id"));
    vertx.eventBus().send("kabassu.database.mongo.replacedocument", updateRequest);
  }
}
