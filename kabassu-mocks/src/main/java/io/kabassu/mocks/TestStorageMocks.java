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

package io.kabassu.mocks;

import io.kabassu.commons.dataobjects.TestStorageInfo;
import io.kabassu.commons.dataobjects.TestStorageInfoBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;

public class TestStorageMocks {

  private static final String JUNIT_RUNNER = "junit";

  private static final String[] TEST_CLASSES = new String[]{"io.kabassu.testexamples.SampleTest",
      "io.kabassu.testexamples.AnotherSampleTest"};

  public static List<TestStorageInfo> createTestInfo() {
    return IntStream.range(0, TEST_CLASSES.length).mapToObj(
        index -> new TestStorageInfoBuilder().setClassName(TEST_CLASSES[index]).setId(index + "")
            .setRunner(JUNIT_RUNNER).setName(
                StringUtils.substringAfterLast(TEST_CLASSES[index], ".")).createTestStorageInfo())
        .collect(
            Collectors.toList());
  }

  public static TestStorageInfo createSingleTestInfo(String id, String className) {
    return new TestStorageInfoBuilder().setClassName(className).setId(id)
        .setRunner(JUNIT_RUNNER).setName(
            StringUtils.substringAfterLast(className, ".")).createTestStorageInfo();
  }


  private TestStorageMocks() {
  }
}
