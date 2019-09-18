package io.kabassu.setup.handlers;

import io.kabassu.setup.configuration.KabassSetupConfiguration;
import io.kabassu.setup.configuration.User;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;

public class SetupHandler implements Handler<Message<Object>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupHandler.class);


  private Vertx vertx;

  private KabassSetupConfiguration options;

  public SetupHandler(Vertx vertx, KabassSetupConfiguration options) {
    this.vertx = vertx;
    this.options = options;
  }

  @Override
  public void handle(Message<Object> event) {
    LOGGER.info("Setup started");
    List<Promise<JsonObject>> createdUserPromises = new ArrayList<>();
    options.getUsers().forEach(user -> createdUserPromises.add(createUser(user)));

    CompositeFuture.all(createdUserPromises.stream().map(Promise::future)
      .collect(Collectors.toList()))
      .setHandler(
        completedFutures -> LOGGER.info("Setup finished. Please change setupMode to false"));
  }

  private Promise<JsonObject> createUser(User user) {
    Promise<JsonObject> promise = Promise.promise();

    vertx.eventBus()
      .request("kabassu.database.mongo.adduser",
        new JsonObject()
          .put("username", user.getUsername())
          .put("password_hash",
            DigestUtils.sha256Hex(user.getPassword())),
        eventResponse -> promise.complete()
      );
    try {
      return promise;
    } catch (Exception e) {
      LOGGER.error("Error during creating user.", e);
      promise.complete(new JsonObject());
      return promise;
    }
  }
}


