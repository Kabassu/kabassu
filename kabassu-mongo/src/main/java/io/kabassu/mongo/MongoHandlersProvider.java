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

package io.kabassu.mongo;

import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.kabassu.mongo.handlers.MongoAddDataHandler;
import io.kabassu.mongo.handlers.MongoGetAllByFieldHandler;
import io.kabassu.mongo.handlers.MongoGetAllHandler;
import io.kabassu.mongo.handlers.MongoGetByFiltersHandler;
import io.kabassu.mongo.handlers.MongoGetDataByIdHandler;
import io.kabassu.mongo.handlers.MongoReplaceDocumentHandler;
import io.kabassu.mongo.handlers.MongoUpdateArrayHandler;
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

  public MongoGetAllHandler provideGetAllHandler() {
    return new MongoGetAllHandler(vertx, configuration);
  }

  public MongoGetAllByFieldHandler provideGetAllByFieldHandler() {
    return new MongoGetAllByFieldHandler(vertx, configuration);
  }

  public MongoReplaceDocumentHandler provideReplaceDocumentHandler() {
    return new MongoReplaceDocumentHandler(vertx, configuration);
  }

  public MongoGetByFiltersHandler provideGetByFilterstHandler() {
    return new MongoGetByFiltersHandler(vertx, configuration);
  }

  public MongoUpdateArrayHandler provideUpdateArrayHandler() {
    return new MongoUpdateArrayHandler(vertx, configuration);
  }
}
