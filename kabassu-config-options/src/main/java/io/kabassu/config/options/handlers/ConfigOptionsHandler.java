package io.kabassu.config.options.handlers;

import io.kabassu.config.options.configuration.KabassuConfigOptionsConfiguration;
import io.kabassu.config.options.reader.OptionsReader;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.List;
import java.util.Map;

public class ConfigOptionsHandler implements Handler<Message<JsonObject>> {

  private Map<String, Map<String, List<String>>> optionsMap;

  public ConfigOptionsHandler(
    KabassuConfigOptionsConfiguration kabassuConfigOptionsConfiguration) {
    this.optionsMap = new OptionsReader(kabassuConfigOptionsConfiguration).read();
  }

  @Override
  public void handle(Message<JsonObject> event) {

  }
}
