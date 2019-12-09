package io.kabassu.results.retriever.main.reports;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.results.retriever.main.configuration.ReportRetrieverConfiguration;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class AETRetriever extends ReportsRetriever {

  private static final String AET_RESPONSE = "aetResponse";
  private static final String CORRELATION_ID = "correlationId";

  private String path;


  protected AETRetriever(ReportRetrieverConfiguration configuration) {
    super(configuration);
  }

  @Override
  public String retrieveReport() throws IOException, InterruptedException {
    File directory = new DirectoryCreator()
      .prepareDirectory(configuration.getReportDownload(), configuration.getName(),
        configuration.getReportType());
    FileUtils.writeStringToFile(new File(directory, "index.html"), fillTemplate(),
      Charset.defaultCharset());
    path = directory.getCanonicalPath();
    return path;
  }

  private String fillTemplate() throws IOException {
    String report;
    JsonObject testData = configuration.getAdditionalData();
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
      Map<String, String> allParameters = ConfigurationRetriever
        .mergeParametersToMap(testData.getJsonObject(JsonFields.DEFINITION),
          testData.getJsonObject(JsonFields.TEST_REQUEST));
      String server =
        allParameters.getOrDefault("server", StringUtils.EMPTY)
          + ":" + allParameters.getOrDefault("port" ,"8181");
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
    return StringUtils
      .substringAfterLast(path, new File(configuration.getReportDownload()).getCanonicalPath())
      + "/index.html";
  }
}
