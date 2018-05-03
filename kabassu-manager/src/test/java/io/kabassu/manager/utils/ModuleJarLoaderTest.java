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

package io.kabassu.manager.utils;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class ModuleJarLoaderTest {

  @Test
  void testCheckClasspath() throws URISyntaxException {
    URL dummyJar = getClass().getClassLoader().getResource("classpath/dummy.jar");
    ModuleJarLoader.checkClasspath(new File(dummyJar.toURI()).getParent()+"/");
    assertThat(System.getProperty("java.class.path"),containsStringIgnoringCase(
        "classpath/dummy.jar"));
  }
}