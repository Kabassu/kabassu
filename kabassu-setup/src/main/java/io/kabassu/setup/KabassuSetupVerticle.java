package io.kabassu.setup;

import io.kabassu.setup.configuration.KabassSetupConfiguration;
import io.kabassu.setup.handlers.SetupHandler;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

public class KabassuSetupVerticle extends AbstractVerticle {

  private MessageConsumer<Object> consumer;

  KabassSetupConfiguration options;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    options = new KabassSetupConfiguration(config());
  }

  @Override
  public void start() throws Exception {
    consumer = vertx.eventBus()
      .consumer("kabassu.setup", new SetupHandler(vertx, options));
  }

  @Override
  public void stop() throws Exception {
    consumer.unregister();
  }

}
