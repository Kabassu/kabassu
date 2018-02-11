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

package io.kabassu.storage.memory.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.kabassu.commons.dataobjects.TestStorageInfo;
import io.kabassu.commons.dataobjects.TestStorageInfoBuilder;
import io.kabassu.mocks.TestStorageMocks;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class MemoryStorageTest {

  private static final String RETURN_JSON = "{\"id\":\"0\",\"name\":\"SampleTest\",\"className\":\"io.kabassu.testexamples.SampleTest\",\"runner\":\"junit\"}";

  private MemoryStorage instance = MemoryStorage.instance();

  @AfterEach
  public void tearDown() {
    instance.cleanStorage();
  }

  @Test
  public void testCleanup() {
    instance.addTest(new TestStorageInfoBuilder().setId("1").createTestStorageInfo());
    instance.addTest(new TestStorageInfoBuilder().setId("2").createTestStorageInfo());
    instance.addTest(new TestStorageInfoBuilder().setId("3").createTestStorageInfo());
    instance.cleanStorage();
    assertEquals(0, instance.getAllTests().size());
  }

  @Test
  public void testRemoveTest() {
    instance.addTest(new TestStorageInfoBuilder().setId("1").createTestStorageInfo());
    instance.addTest(new TestStorageInfoBuilder().setId("2").createTestStorageInfo());
    instance.addTest(new TestStorageInfoBuilder().setId("3").createTestStorageInfo());
    instance.removeTest("2");
    assertEquals(2, instance.getAllTests().size());
    assertEquals(null, instance.getTestById("2"));
  }

  @Test
  public void testAddTest() {
    instance.addTest(new TestStorageInfoBuilder().setId("1").createTestStorageInfo());
    instance.addTest(new TestStorageInfoBuilder().setId("2").createTestStorageInfo());
    instance.addTest(new TestStorageInfoBuilder().setId("3").createTestStorageInfo());
    assertEquals(3, instance.getAllTests().size());
  }

  @Test
  public void testAddTests() {
    List<TestStorageInfo> testData = new ArrayList<>();
    testData.add(new TestStorageInfoBuilder().setId("1").createTestStorageInfo());
    testData.add(new TestStorageInfoBuilder().setId("2").createTestStorageInfo());
    testData.add(new TestStorageInfoBuilder().setId("3").createTestStorageInfo());
    instance.addTests(testData);
    assertEquals(3, instance.getAllTests().size());
  }

  @Test
  public void testSameIdTests() {
    instance.addTest(new TestStorageInfoBuilder().setId("1").createTestStorageInfo());
    boolean addSecondTest = instance
        .addTest(new TestStorageInfoBuilder().setId("1").createTestStorageInfo());
    assertEquals(1, instance.getAllTests().size());
    assertFalse(addSecondTest);
  }

  @Test
  public void testToJson() {
    instance.addTest(TestStorageMocks.createTestInfo().get(0));
    JsonObject testByIdAsJson = instance.getTestByIdAsJson("0");
    assertEquals(RETURN_JSON, testByIdAsJson.encode());
  }

}