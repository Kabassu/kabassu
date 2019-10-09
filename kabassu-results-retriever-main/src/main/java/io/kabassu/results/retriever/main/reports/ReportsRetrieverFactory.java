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

package io.kabassu.results.retriever.main.reports;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

public class ReportsRetrieverFactory {

  private final String downloadDir;

  public ReportsRetrieverFactory(String downloadDir) {
    this.downloadDir = downloadDir;
  }

  public ReportsRetriever getReportsRetriever(String reportType, JsonObject testData) {
    String name = testData.getJsonObject(JsonFields.TEST_REQUEST).getString("_id");
    String reportDir =
      ConfigurationRetriever.getParameter(testData.getJsonObject(JsonFields.DEFINITION), "location")
        + formatReportDir(ConfigurationRetriever
        .getParameter(testData.getJsonObject(JsonFields.DEFINITION), "reportDir"));
    String startItem = ConfigurationRetriever
      .getParameter(testData.getJsonObject(JsonFields.DEFINITION), "startHtml");

    if (reportType.equals("allure")) {
      return new SimpleDownloadRetriever(reportType, name,
        reportDir + "/build/reports/allure-report", downloadDir);
    }
    if (reportType.equals("allure-trend")) {
      return new AllureTrendRetriever(reportType, name, reportDir + "/build/allure-results",
        downloadDir);
    }
    if (reportType.equals("cucumber")) {
      return new SimpleDownloadRetriever(reportType, name,
        reportDir + "/build/cucumber-html-report", downloadDir);
    }
    if (reportType.equals("generic")) {
      return new SimpleDownloadRetriever(reportType, name, reportDir, downloadDir, startItem);
    }
    if (reportType.equals("aet")) {
      return new AETRetriever(reportType, name, reportDir, downloadDir,testData);
    }
    throw new IllegalArgumentException("Unknown type of report:" + reportType);
  }

  private String formatReportDir(String reportDir) {
    return StringUtils.isNotBlank(reportDir) ? "/" + reportDir : StringUtils.EMPTY;
  }
}
