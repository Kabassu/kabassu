package io.kabassu.mongo.handlers;

import io.kabassu.commons.constants.JsonFields;
import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class MongoGetAllHandler extends AbstractMongoGetManyHandler<JsonObject> {

  public MongoGetAllHandler(Vertx vertx,
      KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject request = event.body();
    Promise<JsonObject> getDataPromise = getData(request, new JsonObject());
    Promise<Long> countItemsPromise = countItems(request.getString(JsonFields.COLLECTION),
        new JsonObject());
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
