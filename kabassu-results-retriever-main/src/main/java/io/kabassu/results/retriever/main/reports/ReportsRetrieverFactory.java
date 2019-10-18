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
import io.kabassu.results.retriever.main.configuration.KabassuResultsRetrieverMainConfiguration;
import io.kabassu.results.retriever.main.configuration.ReportRetrieverConfiguration;
import io.kabassu.results.retriever.main.configuration.ReportRetrieverConfigurationBuilder;
import io.vertx.core.json.JsonObject;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.StringUtils;

public class ReportsRetrieverFactory {

  private final KabassuResultsRetrieverMainConfiguration retrieverMainConfiguration;

  public ReportsRetrieverFactory(
    KabassuResultsRetrieverMainConfiguration retrieverMainConfiguration) {
    this.retrieverMainConfiguration = retrieverMainConfiguration;
  }

  public ReportsRetriever getReportsRetriever(String reportType, JsonObject testData)  {

    if (retrieverMainConfiguration.getReportsTypes().containsKey(reportType)) {
      ReportRetrieverConfiguration configuration = mergeReportRetrieverConfiguration(reportType,
        retrieverMainConfiguration.getDefaultReportsDir(), testData,
        retrieverMainConfiguration.getReportsTypes().get(reportType));
      try {
        return (ReportsRetriever) Class
          .forName(retrieverMainConfiguration.getReportsTypes().get(reportType).getString("class"))
          .getDeclaredConstructor(ReportRetrieverConfiguration.class).newInstance(configuration);
      } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        throw new IllegalArgumentException("Problems with report retrievers configuration", e);
      }
    }
    throw new IllegalArgumentException("Unknown type of report:" + reportType);

  }

  private ReportRetrieverConfiguration mergeReportRetrieverConfiguration(String reportType,
    String defaultReportsDir, JsonObject testData, JsonObject entries) {

    String name = testData.getJsonObject(JsonFields.TEST_REQUEST).getString("_id");
    String reportDir =
      ConfigurationRetriever.getParameter(testData.getJsonObject(JsonFields.DEFINITION), "location")
        + formatReportDir(ConfigurationRetriever
          .getParameter(testData.getJsonObject(JsonFields.DEFINITION), "reportDir"),
        entries.getString("reportDir", StringUtils.EMPTY));
    String startItem = ConfigurationRetriever
      .getParameter(testData.getJsonObject(JsonFields.DEFINITION), "startHtml");

    return new ReportRetrieverConfigurationBuilder()
      .setAdditionalData(testData)
      .setName(name)
      .setReportType(reportType)
      .setReportDir(reportDir)
      .setReportDownload(defaultReportsDir)
      .setStartItem(StringUtils.isNotBlank(reportDir) ? startItem
        : entries.getString("startItem", "index.html"))
      .createReportRetrieverConfiguration();
  }

  private String formatReportDir(String reportDir, String dirFromConfig) {
    return StringUtils.isNotBlank(reportDir) ? "/" + reportDir : dirFromConfig;
  }
}
