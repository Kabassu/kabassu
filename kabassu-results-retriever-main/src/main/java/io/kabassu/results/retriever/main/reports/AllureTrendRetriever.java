package io.kabassu.results.retriever.main.reports;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class AllureTrendRetriever extends ReportsRetriever {

  private String path;

  protected AllureTrendRetriever(String reportType, String name, String reportDir,
    String reportConfiguration) {
    super(reportType, name, reportDir, reportConfiguration);
  }

  @Override
  public String retrieveReport() throws IOException, InterruptedException {
    File[] directories = prepareDirectories();
    FileUtils.copyDirectory(new File(reportDir), directories[0]);
    path = directories[1].getCanonicalPath();
    if(new File(directories[1],"history").exists()){
      prepareHistory(directories);
    }
    generateReport(directories[2]);
    return path;
  }

  public String retrieveLink() throws IOException {
    return StringUtils.substringAfterLast(path,new File(reportDownload).getCanonicalPath()) + "/index.html";
  }

  private void generateReport(File directory) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(directory);
    if(SystemUtils.IS_OS_WINDOWS){
      processBuilder.command("cmd.exe", "/c", "allure generate --clean");
    } else {
      processBuilder.command("bash", "-c", "allure generate --clean");
    }
    Process process = processBuilder.start();
    process.waitFor();
  }

  private void prepareHistory(File[] directories) throws IOException {
    File history = new File(directories[0],"history");
    FileUtils.forceMkdir(history);
    FileUtils.copyDirectory(new File(directories[1], "history"),history);
  }

  private File[] prepareDirectories() throws IOException {
    File reportDirectory = new File(reportDownload, name + "-" + reportType);
    FileUtils.forceMkdir(reportDirectory);
    File[] directories = new File[3];
    directories[0] = new File(reportDirectory, "allure-results");
    if (directories[0].exists()) {
      FileUtils.forceDelete(directories[0]);
    }
    FileUtils.forceMkdir(directories[0]);
    directories[1] = new File(reportDirectory, "allure-report");
    if (!directories[1].exists()) {
      FileUtils.forceMkdir(directories[1]);
    }
    directories[2] = reportDirectory;
    return directories;
  }



}
