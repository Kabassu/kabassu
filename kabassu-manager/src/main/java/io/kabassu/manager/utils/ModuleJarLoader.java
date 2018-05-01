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

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class ModuleJarLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModuleJarLoader.class);

  private static final String JAVA_CLASS_PATH = "java.class.path";

  private ModuleJarLoader() {
    //constructor for util class
  }

  public static void checkClasspath(String modulesPath) {
    List<String> jars = Arrays
        .asList(StringUtils.split(System.getProperty(JAVA_CLASS_PATH), ";"));
    File directory = new File(modulesPath);
    List<File> files = Arrays.asList(directory.listFiles());
    files.stream()
        .filter(file -> file.getName().endsWith(".jar"))
        .filter(file -> !jars.contains(modulesPath + file.getName()))
        .forEach(jar -> loadLibrary(jar,modulesPath));

  }

  private static void loadLibrary(File jar,String modulePath) {
    try {
      java.net.URLClassLoader loader = (java.net.URLClassLoader) ClassLoader.getSystemClassLoader();
      java.net.URL url = jar.toURI().toURL();
      for (java.net.URL it : java.util.Arrays.asList(loader.getURLs())) {
        if (it.toURI().equals(url.toURI())) {
          return;
        }
      }
      java.lang.reflect.Method method = java.net.URLClassLoader.class
          .getDeclaredMethod("addURL", java.net.URL.class);
      method.setAccessible(true);
      method.invoke(loader, url);
      System.setProperty(JAVA_CLASS_PATH, System.getProperty(JAVA_CLASS_PATH)+";"+modulePath+jar.getName());
    } catch (final java.lang.NoSuchMethodException |
        java.lang.IllegalAccessException |
        java.net.MalformedURLException |
        java.lang.reflect.InvocationTargetException | URISyntaxException e) {
      LOGGER.error("Can't add jar to classpath: ", e);
    }
  }

}
