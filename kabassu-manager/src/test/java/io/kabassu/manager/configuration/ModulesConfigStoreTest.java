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

package io.kabassu.manager.configuration;

import io.vertx.config.spi.ConfigStore;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.core.Vertx;
import java.io.File;
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ModulesConfigStoreTest {

  Vertx vertx;

  @Test
  public void testGettingConfiguration(TestContext testContext) throws URISyntaxException {
    vertx = Vertx.vertx();
    Async async = testContext.async();
    String configurationDirectory = new File(this.getClass().getClassLoader().getResource("configuration/test.json").toURI()).getParent();
    ConfigStore configStore = new ModuleConfigStoreFactory().create(vertx, new JsonObject()
        .put("path",configurationDirectory ));
    configStore.get(bufferAsyncResult -> {
      JsonObject entries = bufferAsyncResult.result().toJsonObject();
      testContext.assertEquals(entries, getTestEntries());
      async.complete();
    });
  }

  private JsonObject getTestEntries() {
    return new JsonObject().put("modules",new JsonArray().add(new JsonObject().put("test","test")).add(new JsonObject().put("test","test2")));
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }
}