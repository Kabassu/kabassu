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

import io.kabassu.results.retriever.main.configuration.ReportRetrieverConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class LighthouseRetriever extends ReportsRetriever {

  private String path;

  private File directory;

  private static final String TABLE_ENTRY = "<tr><td><a href=\"{entry}\">{entry_name}</a></td></tr>";

  protected LighthouseRetriever(ReportRetrieverConfiguration configuration) {
    super(configuration);
  }

  @Override
  public String retrieveReport() throws IOException {
    directory = new DirectoryCreator()
      .prepareDirectory(configuration.getReportDownload(), configuration.getName(),
        configuration.getReportType());
    FileUtils.copyDirectory(new File(configuration.getReportDir()), directory);
    path = directory.getCanonicalPath();
    return path;
  }

  public String retrieveLink() throws IOException {
    String startItem = "index.html";
    String htmlPath = StringUtils
      .substringAfterLast(path, new File(configuration.getReportDownload()).getCanonicalPath());
    if (directory.listFiles().length == 1 && directory.listFiles()[0].getName().endsWith(".html")) {
      startItem = directory.listFiles()[0].getName();
    } else {
      fillTemplate(htmlPath);
    }

    return htmlPath + "/" + startItem;
  }

  private void fillTemplate(String htmlPath) throws IOException {
    String template = IOUtils.toString(
      getClass().getClassLoader().getResourceAsStream("lighthouse_html.template"),
      Charset.defaultCharset());
    StringBuilder mainEntry = new StringBuilder();
    Arrays.stream(directory.listFiles())
      .forEach(file -> mainEntry.append(prepareEntry("/kabassu/results/"+ htmlPath + "/" + file.getName())));
    FileUtils.writeStringToFile(new File(directory, "index.html"),
      template.replace("{template}", mainEntry),
      Charset.defaultCharset());
  }

  private String prepareEntry(String fileName) {
    return StringUtils.replaceEach(TABLE_ENTRY, new String[]{"{entry}", "{entry_name}"},
      new String[]{fileName, StringUtils.substringAfterLast(fileName, ".")});
  }

}
