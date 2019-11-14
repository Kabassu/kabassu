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


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

class ConfigurationRetrieverTest {

  private JsonObject testJson = prepareTestJson();

  @Test
  void containsParameterTest() {
    assertTrue(ConfigurationRetriever.containsParameter(testJson,"firstParameter"));
    assertTrue(ConfigurationRetriever.containsParameter(testJson,"mirrorParameter"));
    assertTrue(ConfigurationRetriever.containsParameter(testJson,"secondParameter"));
    assertFalse(ConfigurationRetriever.containsParameter(testJson,"thirdParameter"));
  }

  @Test
  void getParameterTest() {
    assertThat(ConfigurationRetriever.getParameter(testJson,"firstParameter"),is("first"));
    assertThat(ConfigurationRetriever.getParameter(testJson,"mirrorParameter"),is("additional"));
    assertThat(ConfigurationRetriever.getParameter(testJson,"secondParameter"),is("second"));
  }

  @Test
  void getAllParametersTest(){
    assertThat(ConfigurationRetriever.getAllParameters(prepareTestJson()).entrySet().size(),is(3));
    assertThat(ConfigurationRetriever.getAllParameters(prepareTestJson()).get("mirrorParameter"),is("additional"));
  }

  @Test
  void mergeParametersToMapTest(){
    assertThat(ConfigurationRetriever.mergeParametersToMap(prepareTestJson(), prepareSecondTestJson()).entrySet().size(),is(4));
    assertThat(ConfigurationRetriever.mergeParametersToMap(prepareTestJson(), prepareSecondTestJson()).get("mirrorParameter"),is("should be this"));
  }


  private JsonObject prepareTestJson() {
    return new JsonObject()
        .put(JsonFields.CONFIGURATION_PARAMETERS, new JsonObject()
            .put("firstParameter", "first")
            .put("mirrorParameter", "configuration"))
        .put(JsonFields.ADDITIONAL_PARAMETERS, new JsonObject()
            .put("secondParameter", "second")
            .put("mirrorParameter", "additional"));
  }

  private JsonObject prepareSecondTestJson() {
    return new JsonObject()
      .put(JsonFields.CONFIGURATION_PARAMETERS, new JsonObject()
        .put("mirrorParameter", "configuration"))
      .put(JsonFields.ADDITIONAL_PARAMETERS, new JsonObject()
        .put("secondParameter", "try")
        .put("thirdParameter", "third")
        .put("mirrorParameter", "should be this"));
  }
}