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

package io.kabassu.manager.configuration;

import io.vertx.config.spi.ConfigProcessor;
import io.vertx.config.spi.ConfigStore;
import io.vertx.config.spi.utils.Processors;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

public class ModulesConfigStore implements ConfigStore {

  private Vertx vertx;

  private File path;

  private final ConfigProcessor processor;

  public ModulesConfigStore(Vertx vertx, JsonObject configuration) {
    this.vertx = vertx;
    String filePath = configuration.getString("path");
    if (filePath == null) {
      throw new IllegalArgumentException("The `path` configuration is required.");
    }
    this.path = new File(filePath);
    if (this.path.isFile()) {
      throw new IllegalArgumentException("The `path` must not be a file");
    }
    this.processor = Processors.get("json");
  }


  @Override
  public void get(Handler<AsyncResult<Buffer>> completionHandler) {
    vertx.<List<File>>executeBlocking(
        fut -> {
          try {
            fut.complete(Arrays.asList(path.listFiles()).stream().filter(File::isFile)
                .collect(Collectors.toList()));
          } catch (Exception e) {
            fut.fail(e);
          }
        },
        ar -> {
          if (ar.failed()) {
            completionHandler.handle(Future.failedFuture(ar.cause()));
          } else {
            buildConfiguration(ar.result(), json -> {
              if (json.failed()) {
                completionHandler.handle(Future.failedFuture(ar.cause()));
              } else {
                completionHandler
                    .handle(Future.succeededFuture(
                        Buffer.buffer(new JsonObject().put("modules", json.result()).encode())));
              }
            });
          }
        }
    );
  }

  private void buildConfiguration(List<File> files, Handler<AsyncResult<JsonArray>> handler) {
    List<Promise> futures = new ArrayList<>();
    files.stream()
        .forEach(file -> {
          Promise<JsonObject> future = Promise.promise();
          futures.add(future);
          try {
            vertx.fileSystem().readFile(file.getAbsolutePath(),
                buffer -> {
                  if (buffer.failed()) {
                    future.fail(buffer.cause());
                  } else {
                    processor.process(vertx, null, buffer.result(), future.future());
                  }
                });
          } catch (RejectedExecutionException e) {
            future.fail(e);
          }
        });

    CompositeFuture.all(futures.stream().map(Promise::future).collect(Collectors.toList()))
        .setHandler(ar -> {
          if (ar.failed()) {
            handler.handle(Future.failedFuture(ar.cause()));
          } else {
            JsonArray result = new JsonArray();
            futures.stream()
                .map(future -> (JsonObject) future.future().result())
                .forEach(result::add);
            handler.handle(Future.succeededFuture(result));
          }
        });
  }
}
