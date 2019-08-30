/*
 * Copyright (C) 2018 Kabassu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kabassu.mongo;

import com.google.common.collect.Lists;
import io.kabassu.mongo.configuration.KabassuMongoConfiguration;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import java.util.List;

public class KabassuMongoVerticle extends AbstractVerticle {

  private List<MessageConsumer> consumers;

  private KabassuMongoConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new KabassuMongoConfiguration(config());
  }

  @Override
  public void start() throws Exception {
    MongoHandlersProvider mongoHandlersProvider = new MongoHandlersProvider(vertx, configuration);

    consumers = Lists.newArrayList();
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.adddefinition",
        mongoHandlersProvider.provideAddDataHandler("kabassu-definitions")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.addsuiterun",
        mongoHandlersProvider.provideAddDataHandler("kabassu-suite-runs")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.addresults",
        mongoHandlersProvider.provideAddDataHandler("kabassu-results")));
    consumers.add(vertx.eventBus()
        .consumer("kabassu.database.mongo.addsuite",
            mongoHandlersProvider.provideAddDataHandler("kabassu-suites")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.getsuiterun",
        mongoHandlersProvider.provideGetDataByIdHandler("kabassu-suite-runs", "_id")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.getresults",
        mongoHandlersProvider.provideGetDataByIdHandler("kabassu-results", "_id")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.getdefinition",
        mongoHandlersProvider.provideGetDataByIdHandler("kabassu-definitions", "_id")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.getrequest",
        mongoHandlersProvider.provideGetDataByIdHandler("kabassu-requests", "_id")));
    consumers.add(vertx.eventBus()
        .consumer("kabassu.database.mongo.getsuite",
            mongoHandlersProvider.provideGetDataByIdHandler("kabassu-suites", "_id")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.addrequest",
        mongoHandlersProvider.provideAddDataHandler("kabassu-requests")));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.getall",
        mongoHandlersProvider.provideGetAllHandler()));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.getallbyfield",
        mongoHandlersProvider.provideGetAllByFieldHandler()));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.getbyfilters",
        mongoHandlersProvider.provideGetByFilterstHandler()));
    consumers.add(vertx.eventBus()
      .consumer("kabassu.database.mongo.replacedocument",
        mongoHandlersProvider.provideReplaceDocumentHandler()));
  }

  @Override
  public void stop() throws Exception {
    consumers.forEach(MessageConsumer::unregister);
  }

}
