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

package io.kabassu.results.retriever.main;

import io.kabassu.results.retriever.main.configuration.KabassuResultsRetrieverMainConfiguration;
import io.kabassu.results.retriever.main.handlers.KabassuResultsRetrieverMainHandler;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class KabassuResultsRetrieverMainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(KabassuResultsRetrieverMainVerticle.class);

  private KabassuResultsRetrieverMainConfiguration configuration;

  private MessageConsumer<JsonObject> consumer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new KabassuResultsRetrieverMainConfiguration(config());
    prepareDirectory();
  }

  @Override
  public void start() throws Exception {

    consumer = vertx.eventBus()
        .consumer(configuration.getAddress(),
            new KabassuResultsRetrieverMainHandler(vertx, configuration));
  }

  @Override
  public void stop() throws Exception {
    consumer.unregister();
  }

  private void prepareDirectory() {
    try {
      FileUtils.forceMkdir(new File(configuration.getDefaultReportsDir()));
    } catch (IOException e) {
      LOGGER.error("Can't create reports directory", e);
    }
  }
}
