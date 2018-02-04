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

package io.kabassu.testrunner.junit.summary;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class TestsExecutionSummary implements TestExecutionSummary {

  private final long timeStarted;

  private final AtomicLong containersFound = new AtomicLong();

  private final AtomicLong containersStarted = new AtomicLong();

  private final AtomicLong containersSkipped = new AtomicLong();

  private final AtomicLong containersAborted = new AtomicLong();

  private final AtomicLong containersSucceeded = new AtomicLong();

  private final AtomicLong containersFailed = new AtomicLong();

  private final AtomicLong testsFound = new AtomicLong();

  private final AtomicLong testsStarted = new AtomicLong();

  private final AtomicLong testsSkipped = new AtomicLong();

  private final AtomicLong testsAborted = new AtomicLong();

  private final AtomicLong testsSucceeded = new AtomicLong();

  private final AtomicLong testsFailed = new AtomicLong();

  private long timeFinished;

  private TestPlan testPlan;

  public TestsExecutionSummary(TestPlan testPlan) {
    this.timeStarted = System.currentTimeMillis();
    this.testPlan = testPlan;
  }

  @Override
  public long getTimeStarted() {
    return timeStarted;
  }

  @Override
  public long getTimeFinished() {
    return timeFinished;
  }

  @Override
  public long getTotalFailureCount() {
    return getTestsFailedCount() + getContainersFailedCount();
  }

  @Override
  public long getContainersFoundCount() {
    return containersFound.get();
  }

  @Override
  public long getContainersStartedCount() {
    return containersStarted.get();
  }

  @Override
  public long getContainersSkippedCount() {
    return containersSkipped.get();
  }

  @Override
  public long getContainersAbortedCount() {
    return containersAborted.get();
  }

  @Override
  public long getContainersSucceededCount() {
    return containersSucceeded.get();
  }

  @Override
  public long getContainersFailedCount() {
    return containersFailed.get();
  }

  @Override
  public long getTestsFoundCount() {
    return testsFound.get();
  }

  @Override
  public long getTestsStartedCount() {
    return testsStarted.get();
  }

  @Override
  public long getTestsSkippedCount() {
    return testsSkipped.get();
  }

  @Override
  public long getTestsAbortedCount() {
    return testsAborted.get();
  }

  @Override
  public long getTestsSucceededCount() {
    return testsSucceeded.get();
  }

  @Override
  public long getTestsFailedCount() {
    return testsFailed.get();
  }

  @Override
  public void printTo(PrintWriter writer) {
    //TODO
  }

  @Override
  public void printFailuresTo(PrintWriter writer) {
    //TODO
  }

  @Override
  public List<Failure> getFailures() {
    return new ArrayList<>();
  }

  public void setTimeFinished(long timeFinished) {
    this.timeFinished = timeFinished;
  }

  public long incrementContainersFound() {
    return containersFound.incrementAndGet();
  }

  public long incrementTestsFound() {
    return testsFound.incrementAndGet();
  }

  public long addContainersSkipped(long skipped) {
    return containersSkipped.addAndGet(skipped);
  }

  public long addTestsSkipped(long skipped) {
    return testsSkipped.addAndGet(skipped);
  }

  public long incrementContainersStarted() {
    return containersStarted.incrementAndGet();
  }

  public long incrementTestsStarted() {
    return testsStarted.incrementAndGet();
  }

  public long incrementContainersSucceeded() {
    return containersSucceeded.incrementAndGet();
  }

  public long incrementTestsSucceeded() {
    return testsSucceeded.incrementAndGet();
  }

  public long incrementContainersAborted() {
    return containersAborted.incrementAndGet();
  }

  public long incrementTestsAborted() {
    return testsAborted.incrementAndGet();
  }

  public long incrementContainersFailed() {
    return containersFailed.incrementAndGet();
  }

  public long incrementTestsFailed() {
    return testsFailed.incrementAndGet();
  }

  public TestPlan getTestPlan() {
    return testPlan;
  }

  public void addFailure(TestIdentifier testIdentifier, Throwable throwable) {
    //TODO
  }
}
