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

package io.kabassu.queue.request.sender.handlers;

import io.kabassu.commons.constants.JsonFields;
import io.kabassu.queue.request.sender.configuration.KabassuQueueRequestSenderConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rabbitmq.RabbitMQOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.rabbitmq.RabbitMQClient;

public class QueueRequestSenderHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueueRequestSenderHandler.class);

  private final Vertx vertx;

  private RabbitMQClient rabbitMQClient;

  private KabassuQueueRequestSenderConfiguration configuration;

  public QueueRequestSenderHandler(Vertx vertx,
    KabassuQueueRequestSenderConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
    this.rabbitMQClient = createRabbitMQClient();
    this.rabbitMQClient.start(result -> {
      if (result.succeeded()) {
        LOGGER.info("Started RabbitMQClient");
      } else {
        LOGGER.error("Problem with RabbitMQ Server", result.cause());
      }
    });
  }

  @Override
  public void handle(Message<JsonObject> event) {
    rabbitMQClient.basicPublish(configuration.getExchange(), retrieveRoutingKey(event.body()),
      new JsonObject().put("body", event.body().encode()), result -> {
        if (!result.succeeded()) {
          LOGGER.error("Error while sending request to servant", result.cause());
        }
      });
  }

  private String retrieveRoutingKey(JsonObject request) {
    String runner = request.getJsonObject(JsonFields.DEFINITION).getString("runner");
    return configuration.getRoutingKeys().containsKey(runner) ? configuration.getRoutingKeys()
      .get(runner) : configuration.getDefaultRoutingKey();
  }

  private RabbitMQClient createRabbitMQClient() {
    return RabbitMQClient.create(vertx, new RabbitMQOptions()
      .setUser(configuration.getRabbitMQConfig().getUsername())
      .setPassword(configuration.getRabbitMQConfig().getPassword())
      .setHost(configuration.getRabbitMQConfig().getHost())
      .setPort(configuration.getRabbitMQConfig().getPort()));
  }

}
