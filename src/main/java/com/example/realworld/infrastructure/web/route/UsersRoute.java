package com.example.realworld.infrastructure.web.route;

import com.example.realworld.infrastructure.vertx.proxy.UserOperations;
import com.example.realworld.infrastructure.web.model.request.LoginRequest;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

@Component
public class UsersRoute extends AbstractHttpRoute {

  private UserOperations userOperations;

  public UsersRoute(UserOperations userOperations) {
    this.userOperations = userOperations;
  }

  @Override
  public Router configure(Vertx vertx) {
    final String usersApiPath = "/users";
    final Router usersRouter = Router.router(vertx);
    usersRouter.route().handler(BodyHandler.create());
    usersRouter.post(usersApiPath).handler(this::create);
    usersRouter.post(usersApiPath + "/login").handler(this::login);
    return usersRouter;
  }

  private void login(RoutingContext routingContext) {
    LoginRequest loginRequest = getBodyAndValid(routingContext, LoginRequest.class);
    userOperations.login(
        loginRequest, responseOrFail(routingContext, HttpResponseStatus.OK.code()));
  }

  private void create(RoutingContext routingContext) {
    NewUserRequest newUserRequest = getBodyAndValid(routingContext, NewUserRequest.class);
    userOperations.create(
        newUserRequest, responseOrFail(routingContext, HttpResponseStatus.OK.code()));
  }
}
