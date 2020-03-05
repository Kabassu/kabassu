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

package io.kabassu.queue.request.sender.configuration;

import io.vertx.core.json.JsonObject;
import java.util.Map;

public class KabassuQueueRequestSenderConfiguration {

  private RabbitMQConfig rabbitMQConfig;

  private String exchange;

  private String defaultRoutingKey;

  private Map<String, String> routingKeys;

  public KabassuQueueRequestSenderConfiguration(JsonObject config) {
    this.rabbitMQConfig = new RabbitMQConfig(config.getJsonObject("rabbitOptions"));
    this.exchange = config.getString("exchange");
    this.defaultRoutingKey = config.getString("defaultRoutingKey");
    config.getJsonArray("routingKeys").stream().forEach(routingKey -> {
      JsonObject routingKeyConfig = (JsonObject) routingKey;
      this.routingKeys
        .put(routingKeyConfig.getString("runner"), routingKeyConfig.getString("routingKey"));
    });
  }

  public RabbitMQConfig getRabbitMQConfig() {
    return rabbitMQConfig;
  }

  public String getExchange() {
    return exchange;
  }

  public String getDefaultRoutingKey() {
    return defaultRoutingKey;
  }

  public Map<String, String> getRoutingKeys() {
    return routingKeys;
  }
}
