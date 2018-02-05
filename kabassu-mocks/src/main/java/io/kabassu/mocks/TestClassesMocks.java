/*
 * Copyright (C) 2018 Kabassu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kabassu.mocks;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class TestClassesMocks {

  private static final String AVAILABLE_TESTS_JSON = "[\"io.kabassu.testexamples.SampleTest\",\"io.kabassu.testexamples.AnotherSampleTest\"]";

  private static final String TEST_CLASS = "testClass";

  private static final String RUNNER = "runner";

  private static final String JUNIT_RUNNER = "junit";

  public static JsonArray getExistingTests() {
    return new JsonArray(AVAILABLE_TESTS_JSON);
  }

  public static JsonObject getTestInfo(String testName) {
    JsonObject toReturn = new JsonObject();
    toReturn.put(TEST_CLASS, testName);
    toReturn.put(RUNNER, JUNIT_RUNNER);
    return toReturn;
  }

  private TestClassesMocks() {
    //for static class
  }

}
