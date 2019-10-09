package io.kabassu.results.retriever.main.reports;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class DirectoryCreator {

  public File prepareDirectory(String reportDownload, String name, String reportType) throws IOException {
    File reportDirectory = new File(reportDownload, name + "-" + reportType);
    FileUtils.forceMkdir(reportDirectory);
    int counter = 1;
    File downloadDirectory = new File(reportDirectory, counter + "");
    while (downloadDirectory.exists()) {
      counter++;
      downloadDirectory = new File(reportDirectory, counter + "");
    }
    FileUtils.forceMkdir(downloadDirectory);
    return downloadDirectory;
  }

}
