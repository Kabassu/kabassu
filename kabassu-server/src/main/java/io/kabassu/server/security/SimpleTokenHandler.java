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

package io.kabassu.server.security;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public class SimpleTokenHandler implements Handler<RoutingContext> {

  private String token;

  public SimpleTokenHandler(String token) {
    this.token = token;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    if (StringUtils.isEmpty(routingContext.queryParams().get("token")) || !routingContext
        .queryParams().get("token").equals(token)) {
      routingContext.fail(401);
    } else {
      routingContext.next();
    }
  }
}
