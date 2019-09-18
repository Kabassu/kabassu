package io.kabassu.server.security.jwt;

import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;

public final class JWTProvider {

  private static JWTAuth jwtAuth;

  public static void initializeProvider(Vertx vertx, String secret) {
    jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
      .addPubSecKey(new PubSecKeyOptions()
        .setAlgorithm("HS256")
        .setPublicKey(secret)
        .setSymmetric(true)));
  }

  public static JWTAuth getProvider() {
    if (jwtAuth == null) {
      throw new IllegalStateException("JWTAuth was not created");
    }
    return jwtAuth;
  }

  private JWTProvider() {
  }

}
