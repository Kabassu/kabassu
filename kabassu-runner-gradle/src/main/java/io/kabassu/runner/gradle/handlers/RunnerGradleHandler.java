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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
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
    String testResult= "Success";
    JsonObject testDefinition = event.body().getJsonObject("definition");
    try (ProjectConnection connection = GradleConnector.newConnector()
      .forProjectDirectory(new File(testDefinition.getString("location"))).connect()) {
      BuildLauncher buildLauncher = connection.newBuild();
      buildLauncher.forTasks("clean", "test");
      if (event.body().getJsonObject("testRequest").containsKey("jvm")) {
        buildLauncher.setJavaHome(new File(configuration.getJvmsMap()
          .get(event.body().getJsonObject("testRequest").getString("jvm"))));
      }
      buildLauncher.setStandardInput(new ByteArrayInputStream("consume this!".getBytes()));
      buildLauncher.run();
    } catch (BuildException e) {
      LOGGER.error(e);
      testResult = "Failure";
    } finally {
      final String result = testResult;
      JsonObject updateHistory = updateHistory(event.body().getJsonObject("testRequest"), testResult);
      vertx.eventBus().rxRequest("kabassu.database.mongo.replacedocument", updateHistory)
        .toObservable()
        .doOnNext(
          eventResponse->{
            vertx.eventBus().send("kabassu.results.dispatcher",event.body().put("result", result).put("testRequest",updateHistory.getJsonObject("new")));
          }
        ).subscribe();
    }

  }

  private JsonObject updateHistory(JsonObject testRequest, String testResult) {
    testRequest.getJsonArray("history").add(new JsonObject().put("date",new Date().getTime()).put("event","Test finished with: "+testResult));
    return new JsonObject().put("new",testRequest)
      .put("collection","kabassu-requests").put("id",testRequest.getString("_id"));
  }

}
