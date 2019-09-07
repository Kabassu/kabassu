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

public class TestResultsBuilder {

  private long containersStartedCount;
  private long testsStartedCount;
  private long testsSucceededCount;
  private long testsFailedCount;

  public TestResultsBuilder setContainersStartedCount(long containersStartedCount) {
    this.containersStartedCount = containersStartedCount;
    return this;
  }

  public TestResultsBuilder setTestsStartedCount(long testsStartedCount) {
    this.testsStartedCount = testsStartedCount;
    return this;
  }

  public TestResultsBuilder setTestsSucceededCount(long testsSucceededCount) {
    this.testsSucceededCount = testsSucceededCount;
    return this;
  }

  public TestResultsBuilder setTestsFailedCount(long testsFailedCount) {
    this.testsFailedCount = testsFailedCount;
    return this;
  }

  public TestResults createTestResults() {
    return new TestResults(containersStartedCount, testsStartedCount, testsSucceededCount,
        testsFailedCount);
  }
}