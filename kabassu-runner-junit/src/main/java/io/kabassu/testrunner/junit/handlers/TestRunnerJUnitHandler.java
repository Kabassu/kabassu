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

import io.kabassu.commons.dataobjects.TestResults;
import io.kabassu.commons.dataobjects.TestResultsBuilder;
import io.kabassu.testrunner.junit.listeners.TestResultsListener;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.Message;
import java.util.UUID;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class TestRunnerJUnitHandler implements Handler<Message<String>> {

  private Vertx vertx;

  public TestRunnerJUnitHandler(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public void handle(Message<String> event) {

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(
            selectClass(event.body())
        )
        .build();

    TestResultsListener testResultsListener = new TestResultsListener();

    Launcher launcher = LauncherFactory.create();
    launcher.registerTestExecutionListeners(testResultsListener);
    launcher.execute(request);
    event.reply(generateResponse(testResultsListener.getSummary(), event.body()));

  }

  private JsonObject generateResponse(TestExecutionSummary summary, String testClass) {
    JsonObject toReturn = new JsonObject();
    toReturn.put("resultsId", "" + UUID.randomUUID());
    toReturn.put("testClass", testClass);
    toReturn.put("results", JsonObject.mapFrom(createResultsFromSummary(summary)));
    return toReturn;
  }

  private TestResults createResultsFromSummary(TestExecutionSummary summary) {
    return new TestResultsBuilder().setContainersStartedCount(summary.getContainersStartedCount())
        .setTestsFailedCount(summary.getTestsFailedCount())
        .setTestsStartedCount(summary.getTestsStartedCount())
        .setTestsSucceededCount(summary.getTestsSucceededCount()).createTestResults();
  }
}
