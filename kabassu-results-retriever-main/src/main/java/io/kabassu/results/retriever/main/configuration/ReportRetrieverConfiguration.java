package io.kabassu.results.retriever.main.configuration;

import io.vertx.core.json.JsonObject;

public class ReportRetrieverConfiguration {

  private String reportType;
  private String name;
  private String reportDir;
  private String reportDownload;
  private String startItem;
  private JsonObject additionalData;

  public ReportRetrieverConfiguration(String reportType, String name, String reportDir,
    String reportDownload, String startItem, JsonObject additionalData) {
    this.reportType = reportType;
    this.name = name;
    this.reportDir = reportDir;
    this.reportDownload = reportDownload;
    this.startItem = startItem;
    this.additionalData = additionalData;
  }

  public String getReportType() {
    return reportType;
  }


  public String getName() {
    return name;
  }

  public String getReportDir() {
    return reportDir;
  }

  public String getReportDownload() {
    return reportDownload;
  }

  public String getStartItem() {
    return startItem;
  }


  public JsonObject getAdditionalData() {
    return additionalData;
  }
}
