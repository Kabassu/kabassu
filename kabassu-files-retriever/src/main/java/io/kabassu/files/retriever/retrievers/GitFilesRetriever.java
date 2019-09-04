package io.kabassu.files.retriever.retrievers;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

public class GitFilesRetriever extends AbstractFileRetriever {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitFilesRetriever.class);

  private File requestDirectory;
  private File checkoutDirectory;

  public GitFilesRetriever(JsonObject request, String downloadDirectory) {
    super(request, downloadDirectory);
  }

  @Override
  public JsonObject getFiles() {
    try {
      prepareDirectory();
      cloneRepository();
      if (request.getJsonObject("testRequest").getJsonObject("additionalData", new JsonObject())
        .containsKey("branch")) {
        switchBranch(
          request.getJsonObject("testRequest").getJsonObject("additionalData").getString("branch"));
      }
      if(!this.request.getJsonObject("definition").containsKey("additionalParameters")){
        this.request.getJsonObject("definition").put("additionalParameters", new JsonObject());
      }
      this.request.getJsonObject("definition").getJsonObject("additionalParameters")
        .put("location", checkoutDirectory.getCanonicalPath());
    } catch (IOException e) {
      LOGGER.error("Problem with getting files from GIT", e);
    } catch (InterruptedException e) {
      LOGGER.error("Problem with getting files from GIT", e);
      Thread.currentThread().interrupt();
    }
    return this.request;
  }

  private void switchBranch(String branch) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(checkoutDirectory);
    if (SystemUtils.IS_OS_WINDOWS) {
      processBuilder.command("cmd.exe", "/c", "git checkout " + branch);
    } else {
      processBuilder.command("bash", "-c", "git checkout " + branch);
    }
    Process process = processBuilder.start();
    process.waitFor();
    processBuilder = new ProcessBuilder();
    processBuilder.directory(checkoutDirectory);
    if (SystemUtils.IS_OS_WINDOWS) {
      processBuilder.command("cmd.exe", "/c", "git pull");
    } else {
      processBuilder.command("bash", "-c", "git pull");
    }
    process = processBuilder.start();
    process.waitFor();
  }

  private void cloneRepository() throws IOException, InterruptedException {
    String repository = request.getJsonObject("definition").getJsonObject("additionalParameters")
      .getString("repository");
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(requestDirectory);
    if (SystemUtils.IS_OS_WINDOWS) {
      processBuilder.command("cmd.exe", "/c", "git clone " + repository);
    } else {
      processBuilder.command("bash", "-c", "git clone " + repository);
    }
    Process process = processBuilder.start();
    process.waitFor();
    checkoutDirectory = requestDirectory.listFiles()[0];
  }

  private void prepareDirectory() throws IOException {
    requestDirectory = new File(this.downloadDirectory,
      this.request.getJsonObject("testRequest").getString("_id"));
    if (requestDirectory.exists()) {
      FileUtils.forceDelete(requestDirectory);
    }
    FileUtils.forceMkdir(requestDirectory);
  }
}
