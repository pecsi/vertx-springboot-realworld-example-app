package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.service.UsersService;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.model.request.LoginRequest;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.response.UserResponse;
import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class UsersAPIVerticle extends AbstractAPIVerticle {

  private final Logger logger = LoggerFactory.getLogger(getClass());

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

    usersRouter.route(userApiPath).handler(this::jwtHandler);
    usersRouter.get(userApiPath).handler(this::getUser);

    createHttpServer(
        subRouter(usersRouter),
        httpServerAsyncResult -> {
          if (httpServerAsyncResult.succeeded()) {
            logger.info(
                "UsersAPI started on port " + config().getInteger(Constants.SERVER_PORT_KEY));
            startPromise.complete();
          } else {
            startPromise.fail(httpServerAsyncResult.cause());
          }
        });
  }

  private void getUser(RoutingContext routingContext) {
    Long userId = routingContext.get(USER_ID_CONTEXT_KEY);
    usersService.findById(
        userId,
        findByIdAsyncResult -> {
          if (findByIdAsyncResult.succeeded()) {
            User existingUser = findByIdAsyncResult.result();
            response(routingContext, HttpResponseStatus.OK.code(), new UserResponse(existingUser));
          } else {
            routingContext.fail(findByIdAsyncResult.cause());
          }
        });
  }

  private void login(RoutingContext routingContext) {
    LoginRequest loginRequest = getBodyAndValid(routingContext, LoginRequest.class);
    usersService.login(
        loginRequest.getEmail(),
        loginRequest.getPassword(),
        loginAsyncResult -> {
          if (loginAsyncResult.succeeded()) {
            User existingUser = loginAsyncResult.result();
            response(routingContext, HttpResponseStatus.OK.code(), new UserResponse(existingUser));
          } else {
            routingContext.fail(loginAsyncResult.cause());
          }
        });
  }

  private void create(RoutingContext routingContext) {
    NewUserRequest newUserRequest = getBodyAndValid(routingContext, NewUserRequest.class);
    usersService.create(
        newUserRequest.getUsername(),
        newUserRequest.getEmail(),
        newUserRequest.getPassword(),
        createUserAsyncHandler -> {
          if (createUserAsyncHandler.succeeded()) {
            User createdUser = createUserAsyncHandler.result();
            response(routingContext, HttpResponseStatus.OK.code(), new UserResponse(createdUser));
          } else {
            routingContext.fail(createUserAsyncHandler.cause());
          }
        });
  }
}
