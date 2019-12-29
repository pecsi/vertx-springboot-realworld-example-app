package com.example.realworld.infrastructure.web.route;

import com.example.realworld.infrastructure.vertx.proxy.UserOperations;
import com.example.realworld.infrastructure.web.model.request.UpdateUserRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

@Component
public class UserRoute extends AbstractHttpRoute {

  private UserOperations userOperations;

  public UserRoute(UserOperations userOperations) {
    this.userOperations = userOperations;
  }

  @Override
  public Router configure(Vertx vertx) {

    final String userApiPath = "/user";

    final Router userRouter = Router.router(vertx);

    userRouter.route().handler(BodyHandler.create());

    userRouter.route(userApiPath).handler(routingContext -> this.jwtHandler(routingContext, false));
    userRouter.get(userApiPath).handler(this::getUser);
    userRouter.put(userApiPath).handler(this::updateUser);

    return userRouter;
  }

  private void updateUser(RoutingContext routingContext) {
    String userId = routingContext.get(USER_ID_CONTEXT_KEY);
    UpdateUserRequest updateUserRequest = getBodyAndValid(routingContext, UpdateUserRequest.class);
    userOperations.update(
        userId,
        updateUserRequest,
        responseOrFail(routingContext, HttpResponseStatus.OK.code(), true));
  }

  private void getUser(RoutingContext routingContext) {
    String userId = routingContext.get(USER_ID_CONTEXT_KEY);
    userOperations.findById(
        userId, responseOrFail(routingContext, HttpResponseStatus.OK.code(), true));
  }
}
