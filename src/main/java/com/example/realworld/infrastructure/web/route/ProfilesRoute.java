package com.example.realworld.infrastructure.web.route;

import com.google.inject.Singleton;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

import java.util.Optional;

@Singleton
public class ProfilesRoute extends AbstractHttpRoute {

  @Override
  public Router configure(Vertx vertx) {
    final String profilesPath = "/profiles";

    final Router profilesRouter = Router.router(vertx);

    profilesRouter.route().handler(BodyHandler.create());

    profilesRouter
        .route(profilesPath)
        .handler(routingContext -> this.jwtHandler(routingContext, true));

    profilesRouter.get(profilesPath + "/:username").handler(this::getProfile);
    return profilesRouter;
  }

  private void getProfile(RoutingContext routingContext) {
    Optional<Long> userId = userId(routingContext);
  }
}
