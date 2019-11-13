package com.example.realworld.domain.service;

import com.example.realworld.domain.entity.Profile;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface ProfilesService {

  String SERVICE_ADDRESS = "profiles-service-event-bus";

  void getProfile(String username, Long loggedUserId, Handler<AsyncResult<Profile>> handler);

  void follow(Long loggedUserId, String username, Handler<AsyncResult<Profile>> handler);

  void unfollow(Long loggedUserId, String username, Handler<AsyncResult<Profile>> handler);
}
