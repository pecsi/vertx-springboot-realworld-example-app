package com.example.realworld.domain.service;

import com.example.realworld.domain.entity.persistent.User;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface UsersService {

  String SERVICE_ADDRESS = "users-service-event-bus";

  void create(String username, String email, String password, Handler<AsyncResult<User>> handler);

  //  User login(String email, String password);
  //
  //  User findById(Long id);
  //
  //  User update(User user);
  //
  //  User findByUsername(String username);
}
