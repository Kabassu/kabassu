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

package io.kabassu.files.retriever.retrievers;

import io.kabassu.commons.constants.JsonFields;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public abstract class AbstractFileRetriever {

  protected JsonObject request;
  protected String downloadDirectory;

  public AbstractFileRetriever(JsonObject request, String downloadDirectory){
    this.downloadDirectory = downloadDirectory;
    this.request = request;
  }

   public abstract JsonObject getFiles();

  protected File prepareDirectory() throws IOException {
    File requestDirectory = new File(this.downloadDirectory,
      this.request.getJsonObject(JsonFields.TEST_REQUEST).getString("_id"));
    if (requestDirectory.exists()) {
      FileUtils.forceDelete(requestDirectory);
    }
    FileUtils.forceMkdir(requestDirectory);
    return requestDirectory;
  }
}
