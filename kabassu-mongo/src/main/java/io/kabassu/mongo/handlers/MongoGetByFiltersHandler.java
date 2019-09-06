package io.kabassu.mongo.handlers;

import io.kabassu.commons.constants.JsonFields;
import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class MongoGetByFiltersHandler extends AbstractMongoHandler<JsonObject> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoGetByFiltersHandler.class);

  public MongoGetByFiltersHandler(Vertx vertx,
    KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject request = event.body();
    JsonObject filters = buildQuery(request.getJsonArray("filters"));
    Promise<JsonObject> getDataPromise = getData(request, filters);
    Promise<Long> countItemsPromise = countItems(request, filters);
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

  private Promise<JsonObject> getData(JsonObject request, JsonObject filters) {
    Promise<JsonObject> promise = Promise.promise();
    FindOptions findOptions = new FindOptions();
    findOptions.setLimit(request.getInteger("pageSize"));
    findOptions.setSkip(request.getInteger("pageSize") * request.getInteger("page"));
    findOptions.setSort(new JsonObject().put("created", -1));
    client
      .findWithOptions(request.getString(JsonFields.COLLECTION), filters,
        findOptions, res -> {
          if (res.succeeded()) {
            promise.complete(new JsonObject().put("results", res.result()));
          } else {
            promise.complete(new JsonObject().put("error", res.cause().getMessage()));
            LOGGER.error("Problem during retrieving data", res.cause());
          }
        });
    try {
      return promise;
    } catch (Exception e) {
      LOGGER.error("Error during getting data from collection {}",
        request.getString(JsonFields.COLLECTION), e);
      promise.complete(new JsonObject().put("error", e.getMessage()));
      LOGGER.error("Problem during retrieving data", e);
      return promise;
    }
  }

  private JsonObject buildQuery(JsonArray filters) {
    JsonObject query = new JsonObject();
    filters.forEach(filter -> {
      createSingleFilter((JsonObject) filter, query);
    });
    return query;
  }

  private void createSingleFilter(JsonObject filter, JsonObject query) {
    query.put(filter.getString("filterName"),
      new JsonObject().put("$in", filter.getJsonArray("filterValues")));
  }

  private Promise<Long> countItems(JsonObject request, JsonObject filters) {
    Promise<Long> promise = Promise.promise();
    client.count(request.getString(JsonFields.COLLECTION), filters, res -> {
      if (res.succeeded()) {
        long objects = res.result();
        promise.complete(objects);
      } else {
        promise.complete(0L);
      }
    });
    try {
      return promise;
    } catch (Exception e) {
      LOGGER.error("Error during counting objects in collection {}",
        request.getString(JsonFields.COLLECTION), e);
      promise.complete(0l);
      return promise;
    }
  }
}
