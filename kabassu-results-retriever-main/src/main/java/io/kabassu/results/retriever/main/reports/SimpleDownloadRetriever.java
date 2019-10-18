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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SimpleDownloadRetriever extends ReportsRetriever {

  private String path;

  protected SimpleDownloadRetriever(ReportRetrieverConfiguration configuration) {
    super(configuration);
  }

  @Override
  public String retrieveReport() throws IOException {
    File directory = new DirectoryCreator()
      .prepareDirectory(configuration.getReportDownload(), configuration.getName(),
        configuration.getReportType());
    FileUtils.copyDirectory(new File(configuration.getReportDir()), directory);
    path = directory.getCanonicalPath();
    return path;
  }

  public String retrieveLink() throws IOException {
    return StringUtils
      .substringAfterLast(path, new File(configuration.getReportDownload()).getCanonicalPath())
      + "/" + configuration.getStartItem();
  }


}
