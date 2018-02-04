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

package io.kabassu.testrunner.junit.handlers;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import io.kabassu.testrunner.junit.listeners.TestResultsListener;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.UUID;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public class TestRunnerJUnitHandler implements Handler<Message<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestRunnerJUnitHandler.class);

  private Vertx vertx;

  public TestRunnerJUnitHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<String> event) {

    LOGGER.info("Running test: " + event.body());

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(
            selectClass(event.body())
        )
        .build();

    TestResultsListener testResultsListener =  new TestResultsListener();

    Launcher launcher = LauncherFactory.create();
    launcher.registerTestExecutionListeners(testResultsListener);
    launcher.execute(request);
    LOGGER.info("Containers: " + testResultsListener.getSummary().getContainersStartedCount());
    LOGGER.info("Run tests: " + testResultsListener.getSummary().getTestsStartedCount());
    LOGGER.info("Successes: " + testResultsListener.getSummary().getTestsSucceededCount());
    LOGGER.info("Failures: " + testResultsListener.getSummary().getTestsFailedCount());
    event.reply(new JsonObject().put("results", ""+ UUID.randomUUID() +testResultsListener.getSummary().getTestsStartedCount()));

  }
}
