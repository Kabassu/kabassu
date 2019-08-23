package io.kabassu.test.rerun.handlers;

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.commons.constants.MessagesFields;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

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
          event.reply(eventResponse.body());
          if (((JsonObject) eventResponse.body()).containsKey("_id")) {
            vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
              new JsonObject()
                .put(MessagesFields.TESTS_TO_RUN,
                  new JsonArray()
                    .add(transformResponse((JsonObject) eventResponse.body()))));
          }
        }
      ).subscribe();
  }

  private JsonObject transformResponse(JsonObject foundRequest) {
    String id = foundRequest.getString("_id");
    foundRequest.remove("_id");
    return foundRequest.put("id", id);
  }
}
