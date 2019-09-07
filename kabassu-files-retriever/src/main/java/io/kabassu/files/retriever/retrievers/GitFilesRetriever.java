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

package io.kabassu.files.retriever.retrievers;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.CommandLines;
import io.kabassu.commons.constants.JsonFields;
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
      if (ConfigurationRetriever.containsParameter(request.getJsonObject(JsonFields.TEST_REQUEST),"branch")) {
        switchBranch(
          ConfigurationRetriever.getParameter(request.getJsonObject(JsonFields.TEST_REQUEST),"branch"));
      }
      if(!this.request.getJsonObject(JsonFields.DEFINITION).containsKey(JsonFields.ADDITIONAL_PARAMETERS)){
        this.request.getJsonObject(JsonFields.DEFINITION).put(JsonFields.ADDITIONAL_PARAMETERS, new JsonObject());
      }
      this.request.getJsonObject(JsonFields.DEFINITION).getJsonObject(JsonFields.ADDITIONAL_PARAMETERS)
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
      processBuilder.command(CommandLines.CMD, "/c", "git checkout " + branch);
    } else {
      processBuilder.command(CommandLines.BASH, "-c", "git checkout " + branch);
    }
    Process process = processBuilder.start();
    process.waitFor();
    processBuilder = new ProcessBuilder();
    processBuilder.directory(checkoutDirectory);
    if (SystemUtils.IS_OS_WINDOWS) {
      processBuilder.command(CommandLines.CMD, "/c", "git pull");
    } else {
      processBuilder.command(CommandLines.BASH, "-c", "git pull");
    }
    process = processBuilder.start();
    process.waitFor();
  }

  private void cloneRepository() throws IOException, InterruptedException {
    String repository = ConfigurationRetriever.getParameter(request.getJsonObject(JsonFields.DEFINITION),"repository");
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(requestDirectory);
    if (SystemUtils.IS_OS_WINDOWS) {
      processBuilder.command(CommandLines.CMD, "/c", "git clone " + repository);
    } else {
      processBuilder.command(CommandLines.BASH, "-c", "git clone " + repository);
    }
    Process process = processBuilder.start();
    process.waitFor();
    checkoutDirectory = requestDirectory.listFiles()[0];
  }

  private void prepareDirectory() throws IOException {
    requestDirectory = new File(this.downloadDirectory,
      this.request.getJsonObject(JsonFields.TEST_REQUEST).getString("_id"));
    if (requestDirectory.exists()) {
      FileUtils.forceDelete(requestDirectory);
    }
    FileUtils.forceMkdir(requestDirectory);
  }
}
