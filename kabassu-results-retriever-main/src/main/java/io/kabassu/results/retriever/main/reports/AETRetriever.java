package io.kabassu.results.retriever.main.reports;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class AETRetriever extends ReportsRetriever {

  public static final String AET_RESPONSE = "aetResponse";
  public static final String CORRELATION_ID = "correlationId";
  private String path;

  private JsonObject testData;

  protected AETRetriever(String reportType, String name, String reportDir,
    String reportDownload, JsonObject testData) {
    super(reportType, name, reportDir, reportDownload);
    this.testData = testData;
  }

  @Override
  public String retrieveReport() throws IOException, InterruptedException {
    File directory = new DirectoryCreator().prepareDirectory(reportDownload, name, reportType);
    FileUtils.writeStringToFile(new File(directory, "index.html"), fillTemplate(),
      Charset.defaultCharset());
    path = directory.getCanonicalPath();
    return path;
  }

  private String fillTemplate() throws IOException {
    String report = StringUtils.EMPTY;
    if (!testData.getJsonObject(JsonFields.TEST_REQUEST).containsKey(AET_RESPONSE) || !testData
      .getJsonObject(JsonFields.TEST_REQUEST).getJsonObject(AET_RESPONSE)
      .containsKey(CORRELATION_ID)) {
      String template = IOUtils.toString(
        getClass().getClassLoader().getResourceAsStream("aet_error_html.template"),
        Charset.defaultCharset());
      if (!testData.getJsonObject(JsonFields.TEST_REQUEST).containsKey(AET_RESPONSE)) {
        report = StringUtils.replace(template, "{errorMessage}", "No response");
      } else {
        report = StringUtils.replace(template, "{errorMessage}",
          testData.getJsonObject(JsonFields.TEST_REQUEST).getJsonObject(AET_RESPONSE)
            .getString("errorMessage"));
      }
    } else {
      String template = IOUtils.toString(
        getClass().getClassLoader().getResourceAsStream("aet_ok_html.template"),
        Charset.defaultCharset());
      JsonObject aetResponse = testData.getJsonObject(JsonFields.TEST_REQUEST)
        .getJsonObject(AET_RESPONSE);
      String server =
        ConfigurationRetriever.getParameter(testData.getJsonObject(JsonFields.DEFINITION), "server")
          + ":" + (ConfigurationRetriever
          .containsParameter(testData.getJsonObject(JsonFields.DEFINITION), "port")
          ? ConfigurationRetriever
          .getParameter(testData.getJsonObject(JsonFields.DEFINITION), "port") : "8181");
      report = StringUtils.replaceEach(template,
        new String[]{"{" + CORRELATION_ID + "}", "{statusUrl}", "{htmlReportUrl}",
          "{xunitReportUrl}"},
        new String[]{aetResponse.getString(CORRELATION_ID),
          server + aetResponse.getString("statusUrl"),
          aetResponse.getString("htmlReportUrl"),
          server + aetResponse.getString("xunitReportUrl")});
    }
    return report;
  }

  public String retrieveLink() throws IOException {
    return StringUtils.substringAfterLast(path, new File(reportDownload).getCanonicalPath())
      + "/index.html";
  }
}
