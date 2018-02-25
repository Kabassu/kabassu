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

package io.kabassu.server.configuration;

import io.kabassu.commons.modes.SecurityMode;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class RoutingPath {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoutingPath.class);

  private HttpMethod method;

  private String path;

  private String address;

  private String handler;

  private SecurityMode securityMode;

  public RoutingPath(HttpMethod method, String path, String address, String handler,
      String securityMode) {
    this.method = method;
    this.path = path;
    this.address = address;
    this.handler = handler;
    this.securityMode = translateSecurity(securityMode);
  }

  private SecurityMode translateSecurity(String securityMode) {
    try {
      return SecurityMode.valueOf(securityMode.toUpperCase());
    } catch (IllegalArgumentException e) {
      LOGGER.error("Security mode \"{}\" for path \"{}\" is not valid!", securityMode, this.path);
      return SecurityMode.NONE;
    }
  }

  public SecurityMode getSecurityMode() {
    return securityMode;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getHandler() {
    return handler;
  }

  public void setHandler(String handler) {
    this.handler = handler;
  }
}
