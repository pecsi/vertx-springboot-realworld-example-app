package com.example.realworld.infrastructure.context;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

public interface ApplicationContext {
  void instantiateServices(Vertx vertx, JsonObject config);

  <T> T getInstance(Class<T> clazz);
}
