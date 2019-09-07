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

import io.vertx.core.Handler;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.RoutingContext;

public enum ServerHandlers {

  DEFAULT("default"){
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new DefaultServerRoutingHandler(vertx, address);
    }
  },
  AVAILABLE("available"){
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new AvailableServerRoutingHandler(vertx, address);
    }
  },
  RESULTS("results") {
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new ResultsServerRoutingHandler(vertx, address);
    }
  },
  TESTMANAGER("testmanager") {
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new TestManagerServerRoutingHandler(vertx, address);
    }
  },
  ADDDATA("adddata") {
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new AddDataRoutingHandler(vertx, address);
    }
  },
  GETDATABYID("getdatabyid") {
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new GetByIdRoutingHandler(vertx, address);
    }
  },
  GETALL("getall") {
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new GetAllRoutingHandler(vertx, address);
    }
  },
  GETALLFIELDBYID("getallbyfield") {
    @Override
    public Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address) {
      return new GetAllByFieldRoutingHandler(vertx, address);
    }
  };

  private final String label;

  ServerHandlers(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public abstract Handler<RoutingContext> gerRoutingHandler(Vertx vertx, String address);
}
