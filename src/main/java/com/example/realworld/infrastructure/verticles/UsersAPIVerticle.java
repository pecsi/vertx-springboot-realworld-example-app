package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.service.UsersService;
import com.example.realworld.infrastructure.web.model.request.LoginRequest;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.request.UpdateUserRequest;
import com.example.realworld.infrastructure.web.model.response.UserResponse;
import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class UsersAPIVerticle extends AbstractAPIVerticle {

  private UsersService usersService;

  @Inject
  public UsersAPIVerticle(UsersService usersService) {
    this.usersService = usersService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final String usersApiPath = "/users";
    final String userApiPath = "/user";

    final Router usersRouter = Router.router(vertx);

    usersRouter.route().handler(BodyHandler.create());

    usersRouter.post(usersApiPath).handler(this::create);
    usersRouter.post(usersApiPath + "/login").handler(this::login);

    usersRouter
        .route(userApiPath)
        .handler(routingContext -> this.jwtHandler(routingContext, false));
    usersRouter.get(userApiPath).handler(this::getUser);
    usersRouter.put(userApiPath).handler(this::updateUser);

    createHttpServer(subRouter(usersRouter), createHttpServerHandler("Users API", startPromise));
  }

  private void updateUser(RoutingContext routingContext) {
    Long userId = routingContext.get(USER_ID_CONTEXT_KEY);
    UpdateUserRequest updateUserRequest = getBodyAndValid(routingContext, UpdateUserRequest.class);
    usersService.update(
        updateUserRequest.toUser(userId),
        responseOrFail(routingContext, HttpResponseStatus.OK.code(), UserResponse::new));
  }

  private void getUser(RoutingContext routingContext) {
    Long userId = routingContext.get(USER_ID_CONTEXT_KEY);
    usersService.findById(
        userId, responseOrFail(routingContext, HttpResponseStatus.OK.code(), UserResponse::new));
  }

  private void login(RoutingContext routingContext) {
    LoginRequest loginRequest = getBodyAndValid(routingContext, LoginRequest.class);
    usersService.login(
        loginRequest.getEmail(),
        loginRequest.getPassword(),
        responseOrFail(routingContext, HttpResponseStatus.OK.code(), UserResponse::new));
  }

  private void create(RoutingContext routingContext) {
    NewUserRequest newUserRequest = getBodyAndValid(routingContext, NewUserRequest.class);
    usersService.create(
        newUserRequest.getUsername(),
        newUserRequest.getEmail(),
        newUserRequest.getPassword(),
        responseOrFail(routingContext, HttpResponseStatus.OK.code(), UserResponse::new));
  }
}
