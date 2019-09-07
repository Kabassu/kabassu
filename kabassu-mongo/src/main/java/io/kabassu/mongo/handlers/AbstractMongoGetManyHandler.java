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
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;

public abstract class AbstractMongoGetManyHandler<T> extends AbstractMongoHandler<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMongoGetManyHandler.class);

  public AbstractMongoGetManyHandler(Vertx vertx,
      KabassuMongoConfiguration configuration) {
    super(vertx, configuration);
  }

  protected Promise<JsonObject> getData(JsonObject request, JsonObject filters) {
    Promise<JsonObject> promise = Promise.promise();
    client
        .findWithOptions(request.getString(JsonFields.COLLECTION), filters,
            getFindOptions(request), res -> {
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

  protected FindOptions getFindOptions(JsonObject request) {
    FindOptions findOptions = new FindOptions();
    findOptions.setLimit(request.getInteger("pageSize"));
    findOptions.setSkip(request.getInteger("pageSize") * request.getInteger("page"));
    findOptions.setSort(new JsonObject().put("created", -1));
    return findOptions;
  }

  protected Promise<Long> countItems(String collection, JsonObject filters) {
    Promise<Long> promise = Promise.promise();
    client.count(collection, filters, res -> {
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
          collection, e);
      promise.complete(0l);
      return promise;
    }
  }
}
