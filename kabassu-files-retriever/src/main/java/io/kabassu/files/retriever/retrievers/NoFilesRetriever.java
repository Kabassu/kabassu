package io.kabassu.files.retriever.retrievers;

import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class NoFilesRetriever extends AbstractFileRetriever {

  private static final Logger LOGGER = LoggerFactory.getLogger(NoFilesRetriever.class);

  public NoFilesRetriever(JsonObject request, String downloadDirectory) {
    super(request, downloadDirectory);
  }

  @Override
  public JsonObject getFiles() {
    try {
      File requestDirectory = new File(this.downloadDirectory,
        this.request.getJsonObject(JsonFields.TEST_REQUEST).getString("_id"));
      if (requestDirectory.exists()) {
        FileUtils.forceDelete(requestDirectory);
      }
      FileUtils.forceMkdir(requestDirectory);
      this.request.getJsonObject(JsonFields.DEFINITION)
        .getJsonObject(JsonFields.ADDITIONAL_PARAMETERS)
        .put("location", requestDirectory.getCanonicalPath());
    } catch (IOException e) {
      LOGGER.error("Problem with creating temp directory", e);
    }
    return this.request;
  }

}
