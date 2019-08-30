package io.kabassu.files.retriever.retrievers;

import io.vertx.core.json.JsonObject;

public abstract class AbstractFileRetriever {

  protected JsonObject request;
  protected String downloadDirectory;

  public AbstractFileRetriever(JsonObject request, String downloadDirectory){
    this.downloadDirectory = downloadDirectory;
    this.request = request;
  }

   public abstract JsonObject getFiles();
}
