package io.kabassu.files.retriever.handler;

import io.kabassu.files.retriever.configuration.KabassuFilesRetrieverConfiguration;
import io.kabassu.files.retriever.retrievers.AbstractFileRetriever;
import io.kabassu.files.retriever.retrievers.FilesRetrieversFactory;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class FilesRetrieverHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  private final String downloadDirectory;

  public FilesRetrieverHandler(Vertx vertx, KabassuFilesRetrieverConfiguration configuration) {
    this.vertx = vertx;
    this.downloadDirectory = configuration.getDownloadDirectory();
  }

  @Override
  public void handle(Message<JsonObject> event) {
    AbstractFileRetriever fileRetriever = new FilesRetrieversFactory(downloadDirectory)
      .getFileRetriever(event.body());
    JsonObject updateRequest = fileRetriever.getFiles();
    event.reply(updateRequest);
  }
}
