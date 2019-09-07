/*
 *   Copyright (C) 2018 Kabassu
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.kabassu.mongo.handlers;

import io.kabassu.commons.constants.JsonFields;
import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;

public class MongoGetByFiltersHandler extends AbstractMongoGetManyHandler<JsonObject> {

  public MongoGetByFiltersHandler(Vertx vertx,
      KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject request = event.body();
    JsonObject filters = buildQuery(request.getJsonArray("filters"));
    Promise<JsonObject> getDataPromise = getData(request, filters);
    Promise<Long> countItemsPromise = countItems(request.getString(JsonFields.COLLECTION), filters);
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

  private JsonObject buildQuery(JsonArray filters) {
    JsonObject query = new JsonObject();
    filters.forEach(filter ->
        createSingleFilter((JsonObject) filter, query)
    );
    return query;
  }

  private void createSingleFilter(JsonObject filter, JsonObject query) {
    query.put(filter.getString("filterName"),
        new JsonObject().put("$in", filter.getJsonArray("filterValues")));
  }

}
