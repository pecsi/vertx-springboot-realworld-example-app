package com.example.realworld.infrastructure.vertx.proxy;

import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.response.UserResponse;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface UserOperations {

  String SERVICE_ADDRESS = "user-service-event-bus";

  void create(NewUserRequest newUserRequest, Handler<AsyncResult<UserResponse>> handler);

  //  void login(String email, String password, Handler<AsyncResult<UserResponse>> handler);
  //
  //  void findById(Long userId, Handler<AsyncResult<UserResponse>> handler);
  //
  //  void update(ProxyUser user, Handler<AsyncResult<UserResponse>> handler);
  //
  //  void findByUsername(String username, Handler<AsyncResult<UserResponse>> handler);
}
