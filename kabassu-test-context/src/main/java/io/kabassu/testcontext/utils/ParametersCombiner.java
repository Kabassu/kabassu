package io.kabassu.testcontext.utils;

import io.vertx.core.json.JsonObject;

public final class ParametersCombiner {

  private ParametersCombiner() {
  }

  public static void mergeAdditionalParameters(JsonObject configurationBase, JsonObject additionalData ){
    additionalData.getJsonObject("parameters").fieldNames().forEach(parameter->{
      if(!configurationBase.getJsonObject("additionalParameters").containsKey(parameter)){
        configurationBase.getJsonObject("additionalParameters").put(parameter, additionalData.getJsonObject("parameters").getValue(parameter));
      }
    });
  }

}
