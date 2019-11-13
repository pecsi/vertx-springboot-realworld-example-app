package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.entity.Profile;
import com.example.realworld.domain.service.ProfilesService;
import com.example.realworld.domain.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.jdbc.JDBCClient;

public class ProfilesServiceImpl extends AbstractService implements ProfilesService {

  private JDBCClient jdbcClient;
  private UsersService usersService;

  public ProfilesServiceImpl(
      JDBCClient jdbcClient, UsersService usersService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.jdbcClient = jdbcClient;
    this.usersService = usersService;
  }

  @Override
  public void getProfile(
      String username, Long loggedUserId, Handler<AsyncResult<Profile>> handler) {

    usersService.findByUsername(username, result(user -> {}));
  }

  @Override
  public void follow(Long loggedUserId, String username, Handler<AsyncResult<Profile>> handler) {}

  @Override
  public void unfollow(Long loggedUserId, String username, Handler<AsyncResult<Profile>> handler) {}
}
