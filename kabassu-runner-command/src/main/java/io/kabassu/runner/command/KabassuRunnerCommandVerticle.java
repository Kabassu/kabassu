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

package io.kabassu.runner.command;

import io.kabassu.runner.command.configuration.KabassuRunnerCommandConfiguration;
import io.kabassu.runner.command.handlers.RunnerCommandHandler;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

public class KabassuRunnerCommandVerticle extends AbstractVerticle {

  private KabassuRunnerCommandConfiguration configuration;

  private MessageConsumer<JsonObject> consumer;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new KabassuRunnerCommandConfiguration(config());
  }

  @Override
  public void start() throws Exception {

    consumer = vertx.eventBus()
      .consumer(configuration.getAddress(), new RunnerCommandHandler(vertx, configuration));
  }

  @Override
  public void stop() throws Exception {
    consumer.unregister();
  }
}