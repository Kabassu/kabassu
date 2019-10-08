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

package io.kabassu.test.rerun.handlers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import io.kabassu.commons.constants.MessagesFields;
import io.kabassu.commons.testutils.DeploymentOptionsUtils;
import io.kabassu.test.rerun.KabassuTestRerunVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class TestRerunHandlerTest {

  public static final String TEST_ID = "testId";
  public static final String HISTORY = "history";

  Vertx vertx;

  @BeforeEach
  void setUp() {
    vertx = Vertx.vertx();
    vertx.deployVerticle(KabassuTestRerunVerticle.class.getName(),
        DeploymentOptionsUtils.createDeploymentOptionsFromJson(new JsonObject()));
    EventBus eventBus = vertx.eventBus();
    eventBus.consumer("kabassu.database.mongo.getrequest", message ->
        message.reply(new JsonObject()
            .put("_id", message.body())
            .put(HISTORY, new JsonArray()
                .add(new JsonObject()
                    .put("event", "preevent"))))
    );
    eventBus.consumer("kabassu.database.mongo.replacedocument", message ->
        message.reply(new JsonObject()
            .put("_id", message.body())
            .put(HISTORY, new JsonArray()
                .add(new JsonObject()
                    .put("event", "preevent"))))
    );
  }

  @Test
  void testRerunRequest(VertxTestContext vertxTestContext) {
    Checkpoint addTestRequestCheckpoint = vertxTestContext.checkpoint();
    Checkpoint sendRequestCheckpoint = vertxTestContext.checkpoint();

    EventBus eventBus = vertx.eventBus();
    eventBus.consumer("kabassu.test.context", message -> vertxTestContext.verify(() ->{
      JsonObject request = (JsonObject) message.body();
      assertThat(request.getJsonArray(MessagesFields.TESTS_TO_RERUN).getJsonObject(0).getString("_id"), is(
          TEST_ID));
      sendRequestCheckpoint.flag();
    }));

    eventBus.consumer("kabassu.test.context", message -> vertxTestContext.verify(() ->{
      JsonObject request = (JsonObject) message.body();
      assertThat(request.getJsonArray(MessagesFields.TESTS_TO_RERUN).getJsonObject(0).getString("_id"), is(
          TEST_ID));
      sendRequestCheckpoint.flag();
    }));

    vertx.eventBus().request("kabassu.reruntestrequest", new JsonObject().put("requestId", TEST_ID), vertxTestContext.succeeding(
        event -> vertxTestContext.verify(() -> {
          JsonObject response = (JsonObject) event.body();
          assertThat(response.getString("_id"), is(TEST_ID));
          assertThat(response.getString("status"), is("started"));
          assertThat(response.containsKey(HISTORY), is(true));
          assertThat(response.getJsonArray(HISTORY).size(), is(2));
          addTestRequestCheckpoint.flag();
        })
    ));
  }

  @AfterEach
  void tearDown() {
    vertx.close();
  }
}