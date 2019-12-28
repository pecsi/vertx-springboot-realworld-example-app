package com.example.realworld.infrastructure.vertx.proxy;

import com.example.realworld.infrastructure.web.model.request.LoginRequest;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.request.UpdateUserRequest;
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

  void login(LoginRequest loginRequest, Handler<AsyncResult<UserResponse>> handler);

  void findById(String userId, Handler<AsyncResult<UserResponse>> handler);

  void update(
      String currentUserId,
      UpdateUserRequest updateUserRequest,
      Handler<AsyncResult<UserResponse>> handler);
  //
  //  void findByUsername(String username, Handler<AsyncResult<UserResponse>> handler);
}
