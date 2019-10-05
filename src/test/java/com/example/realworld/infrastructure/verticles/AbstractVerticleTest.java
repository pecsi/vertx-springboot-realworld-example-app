package com.example.realworld.infrastructure.verticles;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;

public class AbstractVerticleTest {

  @BeforeAll
  public static void beforeAll(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
        new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }
}
