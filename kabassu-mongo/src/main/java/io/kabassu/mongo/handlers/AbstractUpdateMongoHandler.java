package io.kabassu.mongo.handlers;

import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public abstract class AbstractUpdateMongoHandler<T> extends AbstractMongoHandler<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUpdateMongoHandler.class);

  public AbstractUpdateMongoHandler(Vertx vertx,
    KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  protected void responseHandler(AsyncResult<JsonObject> res, Message<JsonObject> event) {
    if (res.succeeded()) {
      validateResult(event, res);
    } else {
      event.reply(new JsonObject().put("error", res.cause().getMessage()));
      LOGGER.error("Problem during editing data", res.cause());
    }
  }

  protected void validateResult(Message<JsonObject> event, AsyncResult<JsonObject> res) {
    if (res.result() != null) {
      event.reply(res.result());
    } else {
      event.reply(new JsonObject().put("error", "Data not found"));
    }
  }
}
