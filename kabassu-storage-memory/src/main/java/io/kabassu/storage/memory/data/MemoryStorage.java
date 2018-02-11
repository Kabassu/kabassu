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

import io.kabassu.commons.dataobjects.TestStorageInfo;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryStorage {

  private static MemoryStorage instance;

  public static MemoryStorage instance() {
    if (instance == null) {
      instance = new MemoryStorage();
    }
    return instance;
  }

  private Map<String, TestStorageInfo> storage = new HashMap<>();

  private MemoryStorage() {
  }

  public TestStorageInfo getTestById(String id) {
    return storage.get(id);
  }

  public JsonObject getTestByIdAsJson(String id) {
    TestStorageInfo testStorageInfo = storage.get(id);
    return testStorageInfo != null ? JsonObject.mapFrom(testStorageInfo) : null;
  }

  public List<TestStorageInfo> getAllTests() {
    return new ArrayList<>(storage.values());
  }

  public boolean addTest(TestStorageInfo testStorageInfo) {
    return storage.put(testStorageInfo.getId(), testStorageInfo) == null ? true : false;
  }

  public void addTests(Collection<TestStorageInfo> testStorageInfos) {
    for (TestStorageInfo testStorageInfo : testStorageInfos) {
      addTest(testStorageInfo);
    }
  }

  public void removeTest(String id) {
    storage.remove(id);
  }

  public void cleanStorage() {
    storage = new HashMap<>();
  }
}
