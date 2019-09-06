package io.kabassu.mongo.handlers;

import io.kabassu.commons.constants.JsonFields;
import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class MongoGetAllByFieldHandler extends AbstractMongoGetManyHandler<JsonObject> {

  public MongoGetAllByFieldHandler(Vertx vertx,
      KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject request = event.body();
    Promise<JsonObject> getDataPromise = getData(request,
        new JsonObject().put(request.getString("field"), request.getString("value")));
    Promise<Long> countItemsPromise = countItems(request.getString(JsonFields.COLLECTION),
        new JsonObject().put(request.getString("field"), request.getString("value")));
    CompositeFuture
        .all(getDataPromise.future(), countItemsPromise.future())
        .setHandler(
            completedFutures -> {
              if (completedFutures.succeeded()) {
                JsonObject result = getDataPromise.future().result();
                result.put("allItems", countItemsPromise.future().result());
                event.reply(result);
              } else {
                event.reply("ERROR");
              }
            });
  }
}
