/*
 * Copyright (C) 2016 Cognifide Limited
 * Modified (C) 2018 Kabassu
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
package io.kabassu.manager.factory;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.VerticleFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractVerticleFactory implements VerticleFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVerticleFactory.class);

  @Override
  public boolean requiresResolve() {
    return true;
  }

  @Override
  public void resolve(String id, DeploymentOptions deploymentOptions, ClassLoader classLoader,
      Future<String> resolution) {
    String identifier = VerticleFactory.removePrefix(id);
    try {
      String verticle = retrieveVerticle(deploymentOptions, identifier);

      if (isWorker()) {
        deploymentOptions.setWorker(true);
        deploymentOptions.setWorkerPoolName(identifier);
        deploymentOptions.setWorkerPoolSize(5);
        deploymentOptions.setInstances(5);
        LOGGER.info("{} is worker", verticle);
      }
      checkClasspath();
      resolution.complete(verticle);
    } catch (Exception e) {
      resolution.fail(e);
    }
  }

  private void checkClasspath() {
    String pathDirectory = StringUtils.substringBefore(System.getProperty("java.class.path"), "/");
    List<String> jars = Arrays
        .asList(StringUtils.split(System.getProperty("java.class.path"), ";"));
    File directory = new File(pathDirectory);
    List<File> files = Arrays.asList(directory.listFiles());
    files.stream()
        .filter(file -> file.getName().endsWith(".jar"))
        .filter(file -> !jars.contains(pathDirectory + "/" + file.getName()))
        .forEach(this::loadLibrary);

  }

  private void loadLibrary(File jar) {
    try {
      java.net.URLClassLoader loader = (java.net.URLClassLoader)ClassLoader.getSystemClassLoader();
      java.net.URL url = jar.toURI().toURL();
      for (java.net.URL it : java.util.Arrays.asList(loader.getURLs())){
        if (it.toURI().equals(url.toURI())){
          return;
        }
      }
      java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", java.net.URL.class);
      method.setAccessible(true);
      method.invoke(loader, url);
    } catch (final java.lang.NoSuchMethodException |
        java.lang.IllegalAccessException |
        java.net.MalformedURLException |
        java.lang.reflect.InvocationTargetException | URISyntaxException  e){
      throw new IllegalArgumentException("Something wrong with jar adding");
    }
  }

  @Override
  public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
    throw new IllegalStateException("Shouldn't be called");
  }

  protected String retrieveVerticle(DeploymentOptions deploymentOptions, String id) {
    String verticle = deploymentOptions.getConfig().getString("verticle");
    if (StringUtils.isEmpty(verticle)) {
      throw new IllegalArgumentException("There is no verticle cconnected to: " + id);
    }
    return verticle;
  }

  protected abstract boolean isWorker();

}
