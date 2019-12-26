package com.example.realworld.infrastructure.vertx.proxy;

import com.example.realworld.infrastructure.web.model.response.ProfileResponse;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface ProfileOperations {

  String SERVICE_ADDRESS = "profiles-service-event-bus";

  void getProfile(
      String username, String currentUserId, Handler<AsyncResult<ProfileResponse>> handler);
}
