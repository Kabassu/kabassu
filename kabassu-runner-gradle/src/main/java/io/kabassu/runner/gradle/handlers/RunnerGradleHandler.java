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

package io.kabassu.runner.gradle.handlers;

import io.kabassu.commons.configuration.ConfigurationRetriever;
import io.kabassu.commons.constants.JsonFields;
import io.kabassu.runner.AbstractRunner;
import io.kabassu.runner.gradle.configuration.KabassuRunnerGradleConfiguration;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import org.gradle.tooling.BuildException;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

public class RunnerGradleHandler extends AbstractRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerGradleHandler.class);

  private KabassuRunnerGradleConfiguration configuration;

  public RunnerGradleHandler(Vertx vertx, KabassuRunnerGradleConfiguration configuration) {
    super(vertx);
    this.configuration = configuration;
  }

  protected void runTest(JsonObject fullRequest) {
    String testResult = "Failure";
    Map<String, String> allParameters = ConfigurationRetriever
      .mergeParametersToMap(fullRequest.getJsonObject(JsonFields.DEFINITION),
        fullRequest.getJsonObject(JsonFields.TEST_REQUEST));
    try (ProjectConnection connection = GradleConnector.newConnector()
      .forProjectDirectory(
        new File(allParameters.get("location"))).connect()) {
      BuildLauncher buildLauncher = connection.newBuild();
      if (allParameters.containsKey("runnerOptions")) {
        String[] runnerOptions = allParameters.get("runnerOptions").split(" ");
        buildLauncher.forTasks(runnerOptions);
      } else {
        buildLauncher.forTasks(new String[1]);
      }
      if (allParameters.containsKey("jvm")) {
        buildLauncher.setJavaHome(new File(configuration.getJvmsMap()
          .get(allParameters.get("jvm"))));
      }
      buildLauncher.setStandardInput(new ByteArrayInputStream("consume this!".getBytes()));
      buildLauncher.run();
      testResult = "Success";
    } catch (BuildException e) {
      LOGGER.error(e);
    } finally {
      finishRun(fullRequest, testResult);
    }
  }

}
