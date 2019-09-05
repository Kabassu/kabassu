package io.kabassu.commons.configuration;

import io.vertx.core.json.JsonObject;

public final class ConfigurationRetriever {

  private ConfigurationRetriever() {
  }

  public static boolean containsParameter(JsonObject dataWithConfig, String parameter) {
    return dataWithConfig.getJsonObject("configurationParameters", new JsonObject())
      .containsKey(parameter) || dataWithConfig
      .getJsonObject("additionalParameters", new JsonObject()).containsKey(parameter);
  }

  public static String getParameter(JsonObject dataWithConfig, String parameter) {
    if (dataWithConfig.containsKey("configurationParameters") && !dataWithConfig
      .getJsonObject("additionalParameters").containsKey(parameter)) {
      return dataWithConfig.getJsonObject("configurationParameters").getString(parameter);
    }
    return dataWithConfig.getJsonObject("additionalParameters").getString(parameter);
  }

}
