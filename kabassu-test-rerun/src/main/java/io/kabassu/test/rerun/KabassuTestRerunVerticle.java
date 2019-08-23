package io.kabassu.test.rerun;

import io.kabassu.test.rerun.handlers.TestRerunHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

public class KabassuTestRerunVerticle extends AbstractVerticle {

  private MessageConsumer<JsonObject> consumer;

  @Override
  public void start() throws Exception {
    consumer = vertx.eventBus()
      .consumer("kabassu.reruntestrequest", new TestRerunHandler(vertx));
  }

  @Override
  public void stop() throws Exception {
    consumer.unregister();
  }

}
