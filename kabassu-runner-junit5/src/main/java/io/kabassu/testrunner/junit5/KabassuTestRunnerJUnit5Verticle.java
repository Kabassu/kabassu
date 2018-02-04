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

package io.kabassu.testrunner.junit5;

import io.kabassu.testrunner.junit5.configuration.KabassuTestRunnerJUnit5Configuration;
import io.kabassu.testrunner.junit5.handlers.TestRunnerJUnit5Handler;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

public class KabassuTestRunnerJUnit5Verticle extends AbstractVerticle {

  private KabassuTestRunnerJUnit5Configuration configuration;

  private MessageConsumer<String> consumer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new KabassuTestRunnerJUnit5Configuration(config());
  }

  @Override
  public void start() throws Exception {

    consumer = vertx.eventBus()
        .consumer(configuration.getAddress(), new TestRunnerJUnit5Handler(vertx));
  }

  @Override
  public void stop() throws Exception {
    consumer.unregister();
  }
}
