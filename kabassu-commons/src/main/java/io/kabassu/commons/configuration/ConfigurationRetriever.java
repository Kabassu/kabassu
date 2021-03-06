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

package io.kabassu.commons.configuration;

import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public final class ConfigurationRetriever {

  private ConfigurationRetriever() {
  }

  public static boolean containsParameter(JsonObject dataWithConfig, String parameter) {
    return dataWithConfig.getJsonObject(JsonFields.CONFIGURATION_PARAMETERS, new JsonObject())
      .containsKey(parameter) || dataWithConfig
      .getJsonObject(JsonFields.ADDITIONAL_PARAMETERS, new JsonObject()).containsKey(parameter);
  }

  public static String getParameter(JsonObject dataWithConfig, String parameter) {
    if (dataWithConfig.containsKey(JsonFields.CONFIGURATION_PARAMETERS) && !dataWithConfig
      .getJsonObject(JsonFields.ADDITIONAL_PARAMETERS).containsKey(parameter)) {
      return dataWithConfig.getJsonObject(JsonFields.CONFIGURATION_PARAMETERS)
        .getString(parameter, StringUtils.EMPTY);
    }
    return dataWithConfig.getJsonObject(JsonFields.ADDITIONAL_PARAMETERS).getString(parameter,
      StringUtils.EMPTY);
  }

  public static Map<String, String> getAllParameters(JsonObject dataWithConfig) {
    Map<String, String> allParameters = new HashMap<>();
    if (dataWithConfig.containsKey(JsonFields.CONFIGURATION_PARAMETERS)) {
      dataWithConfig
        .getJsonObject(JsonFields.CONFIGURATION_PARAMETERS).stream()
        .forEach(entry -> allParameters.put(entry.getKey(), entry.getValue().toString()));
    }
    dataWithConfig
      .getJsonObject(JsonFields.ADDITIONAL_PARAMETERS).stream()
      .forEach(entry -> allParameters.put(entry.getKey(), entry.getValue().toString()));

    return allParameters;
  }



  public static Map<String, String> mergeParametersToMap(JsonObject dataWithConfig,
    JsonObject secondDataWithConfig) {

    Map<String, String> allParameters = ConfigurationRetriever.getAllParameters(dataWithConfig);
    ConfigurationRetriever.getAllParameters(secondDataWithConfig).entrySet()
      .forEach(entry -> allParameters.put(entry.getKey(), entry.getValue()));

    return allParameters;
  }
}
