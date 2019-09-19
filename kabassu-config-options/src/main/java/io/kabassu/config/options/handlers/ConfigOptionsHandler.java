package io.kabassu.config.options.handlers;

import io.kabassu.config.options.configuration.KabassuConfigOptionsConfiguration;
import io.kabassu.config.options.reader.OptionsReader;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.Map;

public class ConfigOptionsHandler implements Handler<Message<JsonObject>> {

  private Map<String, Map<String, Object>> optionsMap;

  public ConfigOptionsHandler(
    KabassuConfigOptionsConfiguration kabassuConfigOptionsConfiguration) {
    this.optionsMap = new OptionsReader(kabassuConfigOptionsConfiguration).read();
  }

  @Override
  public void handle(Message<JsonObject> event) {
    String file = event.body().getString("file");
    String option = event.body().getString("option");
    if(optionsMap.containsKey(file) && optionsMap.get(file).containsKey(option) ){
      event.reply(optionsMap.get(file).get(option));
    } else {
      event.reply(new JsonObject().put("error", "option not found"));
    }
  }
}
