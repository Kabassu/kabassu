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

package io.kabassu.runner.command.handlers;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.runner.AbstractRunner;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import java.util.Map;

public class RunnerCommandHandler extends AbstractRunner {

  public RunnerCommandHandler(Vertx vertx) {
    super(vertx);
  }

  protected void runTest(JsonObject fullRequest) {
    String testResult = "Failure";
    Map<String, String> allParameters = ConfigurationRetriever
      .mergeParametersToMap(fullRequest.getJsonObject(JsonFields.DEFINITION),
        fullRequest.getJsonObject(JsonFields.TEST_REQUEST));
    if (allParameters.containsKey("runnerOptions")) {
      String runnerOptions = allParameters.get("runnerOptions");

      testResult = runCommand(runnerOptions, fullRequest);
    }
    finishRun(fullRequest, testResult);
  }
}
