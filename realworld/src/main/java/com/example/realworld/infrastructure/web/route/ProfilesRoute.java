package com.example.realworld.infrastructure.web.route;

import com.example.realworld.infrastructure.vertx.proxy.ProfileOperations;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

@Component
public class ProfilesRoute extends AbstractHttpRoute {

  private final String USERNAME = "username";
  private final String GET_PROFILE_PATH = "/:" + USERNAME;
  private final String FOLLOW = GET_PROFILE_PATH + "/follow";

  private ProfileOperations profileOperations;

  public ProfilesRoute(ProfileOperations profileOperations) {
    this.profileOperations = profileOperations;
  }

  @Override
  public Router configure(Vertx vertx) {

    String profilesPath = "/profiles";

    final Router profilesRouter = Router.router(vertx);

    profilesRouter.route().handler(BodyHandler.create());

    profilesRouter
        .route(profilesPath + "/*")
        .handler(routingContext -> jwtHandler(routingContext, true));

    profilesRouter.get(profilesPath + GET_PROFILE_PATH).handler(this::getProfile);

    profilesRouter.post(profilesPath + FOLLOW).handler(this::follow);

    return profilesRouter;
  }

  private void getProfile(RoutingContext routingContext) {
    userId(
        routingContext,
        true,
        (String userId) -> {
          String username = routingContext.pathParam(USERNAME);

          profileOperations.getProfile(
              username, userId, responseOrFail(routingContext, HttpResponseStatus.OK.code()));
        });
  }

  private void follow(RoutingContext routingContext) {
    //    userId(
    //        routingContext,
    //        false,
    //        (Long userId) -> {
    //          String username = routingContext.pathParam(USERNAME);
    //          profilesService.follow(
    //              userId,
    //              username,
    //              responseOrFail(routingContext, HttpResponseStatus.OK.code(),
    // ProfileResponse::new));
    //        });
  }
}
