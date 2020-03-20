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

package io.kabassu.queue.request.receiver;

import io.kabassu.queue.RabbitMQUtils;
import io.kabassu.queue.request.receiver.configuration.KabassuQueueRequestReceiverConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.rabbitmq.RabbitMQClient;
import io.vertx.reactivex.rabbitmq.RabbitMQConsumer;

public class QueueRequestReceiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueueRequestReceiver.class);

  private final Vertx vertx;

  private RabbitMQClient rabbitMQClient;

  private KabassuQueueRequestReceiverConfiguration configuration;

  public QueueRequestReceiver(Vertx vertx,
    KabassuQueueRequestReceiverConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
    this.rabbitMQClient = RabbitMQUtils
      .createRabbitMQClient(vertx, configuration.getRabbitMQConfig());
    this.rabbitMQClient.start(result -> {
      if (result.succeeded()) {
        LOGGER.info("Started RabbitMQClient");
        run();
      } else {
        LOGGER.error("Problem with RabbitMQ Server", result.cause());
      }
    });
  }

  public void run() {
    rabbitMQClient.basicConsumer("runners.all", rabbitMQConsumerAsyncResult -> {
      if (rabbitMQConsumerAsyncResult.succeeded()) {
        LOGGER.info("RabbitMQClient Consumer");
        RabbitMQConsumer mqConsumer = rabbitMQConsumerAsyncResult.result();
        mqConsumer.handler(message -> {
          LOGGER.info("Got message: " + message.body().toString());
        });
      } else {
        rabbitMQConsumerAsyncResult.cause().printStackTrace();
      }
    });
  }

}
