package io.kabassu.files.retriever.retrievers;

import io.vertx.core.json.JsonObject;

public class FilesRetrieversFactory {

  private String downloadDirectory;

  public FilesRetrieversFactory(String downloadDirectory) {
    this.downloadDirectory = downloadDirectory;
  }

  public AbstractFileRetriever getFileRetriever(JsonObject request) {
    String locationType = request.getJsonObject("definition").getString("locationType");
    if (locationType.equals("git")) {
      return new GitFilesRetriever(request, downloadDirectory);
    }
    throw new IllegalArgumentException("Unknown file location type " + locationType);
  }
}
