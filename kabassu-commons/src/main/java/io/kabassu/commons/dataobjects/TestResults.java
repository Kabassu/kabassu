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

package io.kabassu.commons.dataobjects;

public class TestResults {

  private final long containersStartedCount;

  private final long testsStartedCount;

  private final long testsSucceededCount;

  private final long testsFailedCount;

  public TestResults(long containersStartedCount, long testsStartedCount, long testsSucceededCount,
      long testsFailedCount) {
    this.containersStartedCount = containersStartedCount;
    this.testsStartedCount = testsStartedCount;
    this.testsSucceededCount = testsSucceededCount;
    this.testsFailedCount = testsFailedCount;
  }

  public long getContainersStartedCount() {
    return containersStartedCount;
  }

  public long getTestsStartedCount() {
    return testsStartedCount;
  }

  public long getTestsSucceededCount() {
    return testsSucceededCount;
  }

  public long getTestsFailedCount() {
    return testsFailedCount;
  }
}
