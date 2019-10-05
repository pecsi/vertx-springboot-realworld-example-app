package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.entity.User;
import com.example.realworld.domain.service.UsersService;
import com.example.realworld.repository.UserRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class UsersServiceImpl implements UsersService {

  private UserRepository userRepository;

  public UsersServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void create(
      String username, String email, String password, Handler<AsyncResult<User>> handler) {

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);

    userRepository.create(user, handler);
  }
}
