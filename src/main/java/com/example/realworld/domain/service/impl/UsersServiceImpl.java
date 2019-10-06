package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.repository.UserRepository;
import com.example.realworld.domain.service.UsersService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;

public class UsersServiceImpl implements UsersService {

  private UserRepository userRepository;
  private JWTAuth jwtProvider;

  public UsersServiceImpl(UserRepository userRepository, JWTAuth jwtProvider) {
    this.userRepository = userRepository;
    this.jwtProvider = jwtProvider;
  }

  @Override
  public void create(
      String username, String email, String password, Handler<AsyncResult<User>> handler) {

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);

    userRepository.create(
        user,
        createUserAsyncResult -> {
          if (createUserAsyncResult.succeeded()) {

            User resultUser = createUserAsyncResult.result();

            resultUser.setToken(
                jwtProvider.generateToken(new JsonObject().put("sub", resultUser.getId())));

            userRepository.update(
                resultUser,
                updateUserAsyncResult -> {
                  if (updateUserAsyncResult.succeeded()) {

                    userRepository.find(resultUser.getId(), handler);

                  } else {
                    handler.handle(updateUserAsyncResult);
                  }
                });

          } else {

            handler.handle(createUserAsyncResult);
          }
        });
  }
}
