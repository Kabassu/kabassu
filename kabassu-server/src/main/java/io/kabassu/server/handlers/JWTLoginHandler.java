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

package io.kabassu.server.handlers;

import io.kabassu.server.configuration.KabassuServerConfiguration;
import io.kabassu.server.security.jwt.JWTProvider;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public class JWTLoginHandler implements Handler<RoutingContext> {

  private Vertx vertx;

  private String address;

  private KabassuServerConfiguration options;

  public JWTLoginHandler(Vertx vertx, String address, KabassuServerConfiguration options) {
    this.vertx = vertx;
    this.address = address;
    this.options = options;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    String loginInformation = routingContext.getBodyAsString();
    if (StringUtils.isNotEmpty(loginInformation)) {

      JsonObject loginData = new JsonObject(loginInformation);

      String generateToken = JWTProvider.getProvider()
        .generateToken(new JsonObject().put("username", loginData.getString("username")),generateOptions());
      
      routingContext.request().response().putHeader("content-type", "application/json")
        .end(new JsonObject().put("auth token", generateToken).encodePrettily());
      //  addData(definition,routingContext.request().response());
    } else {
      routingContext.request().response().putHeader("content-type", "application/json")
        .end(new JsonObject().put("response", "Login Error").encodePrettily());
    }

  }

  private JWTOptions generateOptions() {
    JWTOptions tokenOptions = new JWTOptions();
    if(options.getExpiresInMinutes()!=null && options.getExpiresInMinutes()>0) {
      tokenOptions.setExpiresInMinutes(options.getExpiresInMinutes());
    }
    return tokenOptions;
  }

  private void addData(String definition, HttpServerResponse response) {
    JsonObject message = new JsonObject(definition);
    vertx.eventBus().rxRequest(address, message).toObservable().doOnNext(eventResponse ->
      response.end(((JsonObject) eventResponse.body()).encodePrettily())
    ).subscribe();
  }
}
