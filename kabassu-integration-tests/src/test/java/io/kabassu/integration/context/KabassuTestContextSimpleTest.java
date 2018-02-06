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

package io.kabassu.integration.context;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import io.kabassu.commons.constants.EventBusAdresses;
import io.kabassu.integration.configuration.DeploymentOptionsUtils;
import io.kabassu.mocks.TestClassesMocks;
import io.kabassu.publisher.json.KabassuPublisherJsonVerticle;
import io.kabassu.resultsdispatcher.KabassuResultsDispatcherVerticle;
import io.kabassu.testcontext.KabassuTestContextVerticle;
import io.kabassu.testrunner.junit.KabassuTestRunnerJUnitVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class KabassuTestContextSimpleTest {

  private static final File RESULTS_DIRECTORY = new File("reports-json");

  private Vertx vertx;

  @Before
  public void setUp(TestContext testContext) {
    FileUtils.deleteQuietly(RESULTS_DIRECTORY);
    vertx = Vertx.vertx();
    vertx.deployVerticle(KabassuTestContextVerticle.class.getName(),
        DeploymentOptionsUtils.createDeploymentOptionsFromJson("{\"runners\":[\n"
            + "        {\n"
            + "          \"runner\":\"junit\",\n"
            + "          \"address\":\"kabassu.runner.junit\"\n"
            + "        }\n"
            + "      ]}"),
        testContext.asyncAssertSuccess());
    vertx.deployVerticle(KabassuTestRunnerJUnitVerticle.class.getName(), DeploymentOptionsUtils
            .createDeploymentOptionsFromJson("{      \"address\": \"kabassu.runner.junit\"\n}"),
        testContext.asyncAssertSuccess());
    vertx.deployVerticle(KabassuResultsDispatcherVerticle.class.getName(), DeploymentOptionsUtils
            .createDeploymentOptionsFromJson("{      \"publishers\":[{\n"
                + "          \"name\":\"standard\",\n"
                + "          \"address\":\"kabassu.publisher.json\"\n"
                + "        }]}"),
        testContext.asyncAssertSuccess());
    vertx.deployVerticle(KabassuPublisherJsonVerticle.class.getName(),
        testContext.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext testContext) {
    FileUtils.deleteQuietly(RESULTS_DIRECTORY);
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void testAvailable(TestContext testContext) {
    Async async = testContext.async();
    vertx.eventBus().send(EventBusAdresses.KABASSU_TEST_CONTEXT,
        new JsonObject().put("message_tests_to_run", createTestToRunJsonArray()));
    await().atMost(10, SECONDS).until(RESULTS_DIRECTORY::exists);
    testContext.assertEquals(RESULTS_DIRECTORY.list().length, 1);
    async.complete();
  }

  private JsonArray createTestToRunJsonArray() {
    List<JsonObject> testsToRun = new ArrayList<>();
    testsToRun.add(TestClassesMocks.getTestInfo("io.kabassu.testexamples.SampleTest"));
    return new JsonArray(testsToRun);
  }

}
