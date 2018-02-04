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

package io.kabassu.testregister;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.kabassu.mocks.TestClassesMocks;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class KabassuTestRegisterVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext testContext) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(KabassuTestRegisterVerticle.class.getName(),
        testContext.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void testTestsToRun(TestContext testContext) {
    Async async = testContext.async();
    JsonObject message = new JsonObject();
    message.put("message_request", "runTests");
    JsonArray testToRunJsonArray = createTestToRunJsonArray();
    message.put("message_tests_to_run", testToRunJsonArray);
    vertx.eventBus().send("kabassu.test.retriever", message, event -> {
      JsonObject body = (JsonObject) event.result().body();
      assertEquals(body.getJsonArray("message_reply").size(),testToRunJsonArray.size());
      body.getJsonArray("message_reply").stream().forEach(entry->{
        JsonObject testInfo = (JsonObject) entry;
        assertEquals(testInfo,TestClassesMocks.getTestInfo(testInfo.getString("testClass")));
      });
      async.complete();
    });
  }

  @Test
  public void testAvailable(TestContext testContext) {
    Async async = testContext.async();
    JsonObject message = new JsonObject();
    message.put("message_request", "returnAvailableTests");
    vertx.eventBus().send("kabassu.test.retriever", message, event -> {
      JsonObject body = (JsonObject) event.result().body();
      assertEquals(body.getJsonArray("message_reply"), TestClassesMocks.getExistingTests());
      async.complete();
    });
  }

  private JsonArray createTestToRunJsonArray() {
    List<String> testsToRun = new ArrayList<>();
    testsToRun.add("io.kabassu.testexamples.SampleTest");
    testsToRun.add("io.kabassu.testexamples.AnotherSampleTest");
    return new JsonArray(testsToRun);
  }
}