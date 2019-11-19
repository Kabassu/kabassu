package io.kabassu.files.retriever.retrievers;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

public class FileSystemRetriever extends AbstractFileRetriever {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemRetriever.class);

  public FileSystemRetriever(JsonObject request, String downloadDirectory) {
    super(request, downloadDirectory);
  }

  @Override
  public JsonObject getFiles() {
    try {
      File requestDirectory = prepareDirectory();
      File oryginalDirectory = new File(ConfigurationRetriever
        .getParameter(this.request.getJsonObject(JsonFields.DEFINITION), "location"));
      FileUtils.copyDirectory(oryginalDirectory, requestDirectory);
      this.request.getJsonObject(JsonFields.DEFINITION)
        .getJsonObject(JsonFields.ADDITIONAL_PARAMETERS)
        .put("location", requestDirectory.getCanonicalPath());
    } catch (IOException e) {
      LOGGER.error("Problem with creating temp directory", e);
    }
    return this.request;
  }

}
