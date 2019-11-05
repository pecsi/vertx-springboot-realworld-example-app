package com.example.realworld.infrastructure.verticles;

import io.vertx.core.Promise;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class ProfilesAPIVerticle extends AbstractAPIVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final String profilesPath = "/profiles";

    final Router profilesRouter = Router.router(vertx);

    profilesRouter.route().handler(BodyHandler.create());

    profilesRouter
        .route(profilesPath)
        .handler(routingContext -> this.jwtHandler(routingContext, true));

    profilesRouter.get(profilesPath + "/:username").handler(this::getProfile);

    createHttpServer(
        subRouter(profilesRouter), createHttpServerHandler("Profiles API", startPromise));
  }

  private void getProfile(RoutingContext routingContext) {
    Long userId = routingContext.get(USER_ID_CONTEXT_KEY);
  }
}
