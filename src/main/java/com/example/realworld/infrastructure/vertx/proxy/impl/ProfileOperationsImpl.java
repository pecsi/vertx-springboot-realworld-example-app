package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.domain.profile.service.ProfileService;
import com.example.realworld.infrastructure.vertx.proxy.ProfileOperations;
import com.example.realworld.infrastructure.web.model.response.ProfileResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class ProfileOperationsImpl extends AbstractOperations implements ProfileOperations {

  private ProfileService profileService;

  public ProfileOperationsImpl(ProfileService profileService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.profileService = profileService;
  }

  @Override
  public void getProfile(
      String username, String currentUserId, Handler<AsyncResult<ProfileResponse>> handler) {
    profileService
        .getProfile(username, currentUserId)
        .subscribe(
            profile -> handler.handle(Future.succeededFuture(new ProfileResponse(profile))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void follow(
      String username, String currentUserId, Handler<AsyncResult<ProfileResponse>> handler) {
    profileService
        .follow(username, currentUserId)
        .subscribe(
            profile -> handler.handle(Future.succeededFuture(new ProfileResponse(profile))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void unfollow(
      String username, String currentUserId, Handler<AsyncResult<ProfileResponse>> handler) {
    profileService
        .unfollow(username, currentUserId)
        .subscribe(
            profile -> handler.handle(Future.succeededFuture(new ProfileResponse(profile))),
            throwable -> handler.handle(error(throwable)));
  }
}
