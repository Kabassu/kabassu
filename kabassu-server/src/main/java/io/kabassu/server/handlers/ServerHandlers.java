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
