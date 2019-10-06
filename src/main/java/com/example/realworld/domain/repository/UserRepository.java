package com.example.realworld.domain.repository;

import com.example.realworld.domain.entity.persistent.User;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface UserRepository {
  String SERVICE_ADDRESS = "user-repository-event-bus";

  void create(User user, Handler<AsyncResult<User>> handler);

  void update(User user, Handler<AsyncResult<User>> handler);

  void find(Long id, Handler<AsyncResult<User>> handler);
}
