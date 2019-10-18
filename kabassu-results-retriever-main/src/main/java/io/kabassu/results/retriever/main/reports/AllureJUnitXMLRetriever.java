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

import io.kabassu.commons.constants.CommandLines;
import io.kabassu.results.retriever.main.configuration.ReportRetrieverConfiguration;
import io.kabassu.results.retriever.main.reports.utils.XMLFileFilter;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class AllureJUnitXMLRetriever extends ReportsRetriever {

  private String path;

  protected AllureJUnitXMLRetriever(ReportRetrieverConfiguration configuration) {
    super(configuration);
  }

  @Override
  public String retrieveReport() throws IOException, InterruptedException {
    File[] directories = prepareDirectories();
    FileUtils
      .copyDirectory(new File(configuration.getReportDir()), directories[0], new XMLFileFilter());
    path = directories[1].getCanonicalPath();
    generateReport(directories[1]);
    return path;
  }

  public String retrieveLink() throws IOException {
    return StringUtils
      .substringAfterLast(path, new File(configuration.getReportDownload()).getCanonicalPath())
      + "/index.html";
  }

  private void generateReport(File directory)
    throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(directory.getParentFile());
    if (SystemUtils.IS_OS_WINDOWS) {
      processBuilder.command(CommandLines.CMD, "/c", "allure generate temporary-results --clean -o " + directory.getName());
    } else {
      processBuilder.command(CommandLines.BASH, "-c", "allure generate temporary-results --clean -o " + directory.getName());
    }
    Process process = processBuilder.start();
    process.waitFor();
  }

  private File[] prepareDirectories() throws IOException {
    File reportDirectory = new File(configuration.getReportDownload(),
      configuration.getName() + "-" + configuration.getReportType());
    FileUtils.forceMkdir(reportDirectory);
    File[] directories = new File[3];
    directories[0] = new File(reportDirectory, "temporary-results");
    if (directories[0].exists()) {
      FileUtils.forceDelete(directories[0]);
    }
    FileUtils.forceMkdir(directories[0]);
    directories[1] = new DirectoryCreator()
      .prepareDirectory(configuration.getReportDownload(), configuration.getName(),
        configuration.getReportType());
    if (!directories[1].exists()) {
      FileUtils.forceMkdir(directories[1]);
    }
    return directories;
  }


}
