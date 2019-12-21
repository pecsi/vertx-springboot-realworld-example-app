package com.example.realworld.infrastructure.web.config;

import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;

public class AuthProviderConfig {

  public static JWTAuth jwtProvider(Vertx vertx, String algorithm, String secret) {

    PubSecKeyOptions pubSecKeyOptions = new PubSecKeyOptions();
    pubSecKeyOptions.setAlgorithm(algorithm);
    pubSecKeyOptions.setPublicKey(secret);
    pubSecKeyOptions.setSymmetric(true);

    JWTAuthOptions jwtAuthOptions = new JWTAuthOptions();
    jwtAuthOptions.addPubSecKey(pubSecKeyOptions);

    return JWTAuth.create(vertx, jwtAuthOptions);
  }
}
