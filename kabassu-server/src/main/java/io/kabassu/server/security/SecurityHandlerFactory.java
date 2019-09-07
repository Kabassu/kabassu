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

import io.kabassu.commons.modes.SecurityMode;
import io.kabassu.server.configuration.KabassuServerConfiguration;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class SecurityHandlerFactory {

  KabassuServerConfiguration kabassuServerConfiguration;

  public SecurityHandlerFactory(
      KabassuServerConfiguration kabassuServerConfiguration) {
    this.kabassuServerConfiguration = kabassuServerConfiguration;
  }

  public Handler<RoutingContext> createSecurityHandler(SecurityMode securityHandlerType) {

    if (securityHandlerType.equals(SecurityMode.TOKEN)) {
      return new SimpleTokenHandler(kabassuServerConfiguration.getSimpleToken());
    }

    throw new IllegalArgumentException("Unknown type of security: " + securityHandlerType);
  }
}
