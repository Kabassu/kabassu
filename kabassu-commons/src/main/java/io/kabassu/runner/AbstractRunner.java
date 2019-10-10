package io.kabassu.runner;

import io.kabassu.commons.checks.FilesDownloadChecker;
import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public abstract class AbstractRunner implements Handler<Message<JsonObject>> {

  protected final Vertx vertx;

  public AbstractRunner(Vertx vertx) {
    this.vertx = vertx;
  }

  public void handle(Message<JsonObject> event) {
    runTestWithFilesValidation(event);
  }

  protected void runTestWithFilesValidation(Message<JsonObject> event){
    if (FilesDownloadChecker
      .checkIfFilesRetrieveIsRequired(
        event.body().getJsonObject(JsonFields.DEFINITION).getString("locationType"))) {
      vertx.eventBus().rxRequest("kabassu.filesretriever",
        event.body()).toObservable()
        .doOnNext(
          eventResponse ->
            runTest((JsonObject) eventResponse.body())
        ).subscribe();
    } else {
      runTest(event.body());
    }
  }

  protected abstract void runTest(JsonObject fullRequest);

}
