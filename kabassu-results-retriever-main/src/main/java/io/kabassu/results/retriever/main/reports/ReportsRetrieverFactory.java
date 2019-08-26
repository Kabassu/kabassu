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

package io.kabassu.results.retriever.main.reports;

public class ReportsRetrieverFactory {

  private final String downloadDir;

  public ReportsRetrieverFactory(String downloadDir) {
    this.downloadDir = downloadDir;
  }

  public ReportsRetriever getReportsRetriever(String reportType, String name, String reportDir){
    if(reportType.equals("allure")){
      return new SimpleDownloadRetriever(reportType, name, reportDir+"/build/reports/allure-report", downloadDir);
    }
    if(reportType.equals("allure-trend")){
      return new AllureTrendRetriever(reportType, name, reportDir+"/build/allure-results", downloadDir);
    }
    throw new IllegalArgumentException("Unknown type of report:" + reportType);
  }
}
