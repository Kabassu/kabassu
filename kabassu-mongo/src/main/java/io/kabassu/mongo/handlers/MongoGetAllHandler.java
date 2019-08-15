package io.kabassu.mongo.handlers;

import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class MongoGetAllHandler extends AbstractMongoHandler<JsonObject> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoGetAllHandler.class);

  public MongoGetAllHandler(Vertx vertx,
    KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject request = event.body();
    FindOptions findOptions = new FindOptions();
    findOptions.setLimit(request.getInteger("pageSize"));
    findOptions.setSkip(request.getInteger("pageSize")*request.getInteger("page"));
    client.findWithOptions(request.getString("collection"), new JsonObject(), findOptions, res->{
      if (res.succeeded()) {
        event.reply(new JsonObject().put("results",res.result()));
      } else {
        event.reply(new JsonObject().put("error", res.cause().getMessage()));
        LOGGER.error("Problem during retrieving data", res.cause());
      }
    });
  }
}
