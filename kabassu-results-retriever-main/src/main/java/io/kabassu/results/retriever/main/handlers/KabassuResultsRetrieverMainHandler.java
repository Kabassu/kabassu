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
    if(testData.getJsonObject("definition").containsKey("reports")){
      reports = testData.getJsonObject("definition").getJsonArray("reports").getList();
    } else {
      reports.add(configuration.getDefaultReports());
    }
    List<String> downloadReports = new ArrayList<>();
    reports.forEach(report->{
      ReportsRetriever reportsRetriever = reportsRetrieverFactory
          .getReportsRetriever(report, testData.getJsonObject("testRequest").getString("id"),
              testData.getJsonObject("definition").getString("location"));
      try {
        downloadReports.add(reportsRetriever.retrieveReport());
      } catch (IOException e) {
        LOGGER.error("Problem with report download", e);
      }

    });
    JsonObject resultsData = new JsonObject();
    resultsData.put("testId", testData.getJsonObject("testRequest").getString("id"));
    resultsData.put("result", testData.getString("result"));
    testData.put("downloadedReports", new JsonArray(downloadReports));
    vertx.eventBus().send("kabassu.database.mongo.addresults",testData);
  }

}
