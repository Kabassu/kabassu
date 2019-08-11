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

package io.kabassu.commons.dataobjects;

public class TestUploadFileBuilder {

  private String name;

  private String uploadedFileName;

  private String fileName;

  private long size;

  private String contentType;

  private String contentTransferEncoding;

  private String charSet;

  public TestUploadFileBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public TestUploadFileBuilder setUploadedFileName(String uploadedFileName) {
    this.uploadedFileName = uploadedFileName;
    return this;
  }

  public TestUploadFileBuilder setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public TestUploadFileBuilder setSize(long size) {
    this.size = size;
    return this;
  }

  public TestUploadFileBuilder setContentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  public TestUploadFileBuilder setContentTransferEncoding(String contentTransferEncoding) {
    this.contentTransferEncoding = contentTransferEncoding;
    return this;
  }

  public TestUploadFileBuilder setCharSet(String charSet) {
    this.charSet = charSet;
    return this;
  }

  public TestUploadFile createTestUploadFile() {
    return new TestUploadFile(name, uploadedFileName, fileName, size, contentType,
        contentTransferEncoding, charSet);
  }
}