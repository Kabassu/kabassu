package io.kabassu.config.options.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Map;

public class ConfigOptionsHandler implements Handler<Message<JsonObject>> {

  public ConfigOptionsHandler(Vertx vertx, Map<String, String[]> availableOptions) {

  }

  @Override
  public void handle(Message<JsonObject> event) {

  }
}
