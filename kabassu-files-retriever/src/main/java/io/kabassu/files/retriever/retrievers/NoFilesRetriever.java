package io.kabassu.files.retriever.retrievers;

import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;

public class NoFilesRetriever extends AbstractFileRetriever {

  private static final Logger LOGGER = LoggerFactory.getLogger(NoFilesRetriever.class);

  public NoFilesRetriever(JsonObject request, String downloadDirectory) {
    super(request, downloadDirectory);
  }

  @Override
  public JsonObject getFiles() {
    try {
      this.request.getJsonObject(JsonFields.DEFINITION)
        .getJsonObject(JsonFields.ADDITIONAL_PARAMETERS)
        .put("location", prepareDirectory().getCanonicalPath());
    } catch (IOException e) {
      LOGGER.error("Problem with creating temp directory", e);
    }
    return this.request;
  }

}
