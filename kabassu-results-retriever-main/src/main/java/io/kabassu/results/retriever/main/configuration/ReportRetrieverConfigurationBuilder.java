package io.kabassu.results.retriever.main.configuration;

import io.vertx.core.json.JsonObject;

public class ReportRetrieverConfigurationBuilder {

  private String reportType;
  private String name;
  private String reportDir;
  private String reportDownload;
  private String startItem;
  private JsonObject additionalData;

  public ReportRetrieverConfigurationBuilder setReportType(String reportType) {
    this.reportType = reportType;
    return this;
  }

  public ReportRetrieverConfigurationBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public ReportRetrieverConfigurationBuilder setReportDir(String reportDir) {
    this.reportDir = reportDir;
    return this;
  }

  public ReportRetrieverConfigurationBuilder setReportDownload(String reportDownload) {
    this.reportDownload = reportDownload;
    return this;
  }

  public ReportRetrieverConfigurationBuilder setStartItem(String startItem) {
    this.startItem = startItem;
    return this;
  }

  public ReportRetrieverConfigurationBuilder setAdditionalData(JsonObject additionalData) {
    this.additionalData = additionalData;
    return this;
  }

  public ReportRetrieverConfiguration createReportRetrieverConfiguration() {
    return new ReportRetrieverConfiguration(reportType, name, reportDir, reportDownload, startItem,
      additionalData);
  }
}