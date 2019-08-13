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

package io.kabassu.runner.gradle.handlers;

import io.kabassu.runner.gradle.configuration.KabassuRunnerGradleConfiguration;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.gradle.tooling.BuildException;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

public class RunnerGradleHandler implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RunnerGradleHandler.class);

  private Vertx vertx;

  private KabassuRunnerGradleConfiguration configuration;

  public RunnerGradleHandler(Vertx vertx, KabassuRunnerGradleConfiguration configuration) {
    this.vertx = vertx;
    this.configuration = configuration;
  }

  @Override
  public void handle(Message<JsonObject> event) {
    JsonObject testDefinition = event.body().getJsonObject("definition");
    // TODO need to switch java version
    try (ProjectConnection connection = GradleConnector.newConnector()
      .forProjectDirectory(new File(testDefinition.getString("location"))).connect()) {
      BuildLauncher buildLauncher = connection.newBuild();
      buildLauncher.forTasks("clean", "test");
      if(event.body().getJsonObject("testRequest").containsKey("jvm")) {
        buildLauncher.setJavaHome(new File(configuration.getJvmsMap().get(event.body().getJsonObject("testRequest").getString("jvm"))));
      }
      buildLauncher.setStandardInput(new ByteArrayInputStream("consume this!".getBytes()));

      //kick the build off:
      buildLauncher.run();
    } catch (BuildException e){
      LOGGER.error(e);
    }
  }

}
