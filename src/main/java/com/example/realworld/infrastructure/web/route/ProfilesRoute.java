package com.example.realworld.infrastructure.web.route;

import com.example.realworld.domain.service.ProfilesService;
import com.example.realworld.infrastructure.web.model.response.ProfileResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

@Singleton
public class ProfilesRoute extends AbstractHttpRoute {

  public static final String USERNAME = "username";
  private ProfilesService profilesService;

  @Inject
  public ProfilesRoute(ProfilesService profilesService) {
    this.profilesService = profilesService;
  }

  @Override
  public Router configure(Vertx vertx) {
    final String profilesPath = "/profiles";

    final Router profilesRouter = Router.router(vertx);

    profilesRouter.route().handler(BodyHandler.create());

    profilesRouter
        .route(profilesPath + "/*")
        .handler(routingContext -> this.jwtHandler(routingContext, true));

    profilesRouter.get(profilesPath + "/:" + USERNAME).handler(this::getProfile);
    return profilesRouter;
  }

  private void getProfile(RoutingContext routingContext) {
    userId(
        routingContext,
        true,
        (Long userId) -> {
          String username = routingContext.pathParam(USERNAME);
          profilesService.getProfile(
              username,
              userId,
              responseOrFail(routingContext, HttpResponseStatus.OK.code(), ProfileResponse::new));
        });
  }
}
