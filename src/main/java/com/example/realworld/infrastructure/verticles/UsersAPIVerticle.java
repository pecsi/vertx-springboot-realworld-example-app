package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.service.UsersService;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.validation.Validator;

public class UsersAPIVerticle extends AbstractAPIVerticle {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final String API_PATH = "/users";

  private UsersService usersService;

  public UsersAPIVerticle(
      UsersService usersService, ObjectMapper objectMapper, Validator validator) {
    super(objectMapper, validator);
    this.usersService = usersService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.post().handler(this::create);

    createHttpServer(
        router,
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
