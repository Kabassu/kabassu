package io.kabassu.mongo;

import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.kabassu.mongo.handlers.MongoAddDataHandler;
import io.kabassu.mongo.handlers.MongoGetDataByIdHandler;
import io.vertx.reactivex.core.Vertx;

public class MongoHandlersProvider {

  private final KabassuMongoConfiguration configuration;
  private final Vertx vertx;

  public MongoHandlersProvider(Vertx vertx, KabassuMongoConfiguration configuration) {
    this.configuration = configuration;
    this.vertx = vertx;
  }

  public MongoAddDataHandler provideAddDataHandler(String collection) {
    return new MongoAddDataHandler(vertx, configuration, collection);
  }

  public MongoGetDataByIdHandler provideGetDataByIdHandler(String collection, String idName) {
    return new MongoGetDataByIdHandler(vertx, configuration, collection, idName);
  }
}
