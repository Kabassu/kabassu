package io.kabassu.test.rerun.handlers;

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.commons.constants.MessagesFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Date;

public class TestRerunHandler implements Handler<Message<JsonObject>> {

  private final Vertx vertx;

  public TestRerunHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    vertx.eventBus()
      .rxRequest("kabassu.database.mongo.getrequest", event.body().getString("requestId"))
      .toObservable()
      .doOnNext(
        eventResponse -> {
          JsonObject updateHistory = updateHistory((JsonObject) eventResponse.body());
          vertx.eventBus().rxRequest("kabassu.database.mongo.replacedocument", updateHistory).toObservable()
            .doOnNext(updateResponse->{
              if (updateHistory.getJsonObject("new").containsKey("_id")) {
                event.reply(updateHistory.getJsonObject("new"));
                vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
                  new JsonObject()
                    .put(MessagesFields.TESTS_TO_RUN,
                      new JsonArray()
                        .add(updateHistory.getJsonObject("new"))));
              }
              }
            ).subscribe();
        }
      ).subscribe();
  }

  private JsonObject updateHistory(JsonObject testRequest) {
    testRequest.put("status","started");
    testRequest.getJsonArray("history").add(new JsonObject().put("date",new Date().getTime()).put("event","Request rerun started"));
    return new JsonObject().put("new",testRequest)
      .put("collection","kabassu-requests").put("id",testRequest.getString("_id"));
  }

}
