package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.domain.user.service.UserService;
import com.example.realworld.infrastructure.vertx.proxy.UserOperations;
import com.example.realworld.infrastructure.web.model.request.LoginRequest;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.request.UpdateUserRequest;
import com.example.realworld.infrastructure.web.model.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class UserOperationsImpl extends AbstractOperations implements UserOperations {

  private UserService userService;

  public UserOperationsImpl(UserService userService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.userService = userService;
  }

  @Override
  public void create(NewUserRequest newUserRequest, Handler<AsyncResult<UserResponse>> handler) {
    userService
        .create(newUserRequest.toNewUser())
        .subscribe(
            user -> handler.handle(Future.succeededFuture(new UserResponse(user))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void login(LoginRequest loginRequest, Handler<AsyncResult<UserResponse>> handler) {
    userService
        .login(loginRequest.toLogin())
        .subscribe(
            user -> handler.handle(Future.succeededFuture(new UserResponse(user))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void findById(String userId, Handler<AsyncResult<UserResponse>> handler) {
    userService
        .findById(userId)
        .subscribe(
            user -> handler.handle(Future.succeededFuture(new UserResponse(user))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void update(
      String currentUserId,
      UpdateUserRequest updateUserRequest,
      Handler<AsyncResult<UserResponse>> handler) {
    userService
        .update(updateUserRequest.toUpdateUser(), currentUserId)
        .subscribe(
            user -> handler.handle(Future.succeededFuture((new UserResponse(user)))),
            throwable -> handler.handle(error(throwable)));
  }
}
