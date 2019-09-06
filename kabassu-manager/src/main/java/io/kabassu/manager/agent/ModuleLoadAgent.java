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

package io.kabassu.manager.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

public class ModuleLoadAgent {

  private static Instrumentation inst;

  private ModuleLoadAgent() {
  }

  public static void addToClassPath(File jarFile) throws IOException {
    inst.appendToSystemClassLoaderSearch(new JarFile(jarFile));
  }

  public static void premain(String agentArgs, Instrumentation instrumentation) {
    inst = instrumentation;
  }

}
