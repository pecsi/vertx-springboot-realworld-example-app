package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.service.UsersService;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.exception.mapper.BusinessExceptionMapper;
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
      UsersService usersService,
      ObjectMapper wrapUnwrapRootValueObjectMapper,
      ObjectMapper defaultObjectMapper,
      Validator validator,
      BusinessExceptionMapper businessExceptionMapper) {
    super(wrapUnwrapRootValueObjectMapper, defaultObjectMapper, validator, businessExceptionMapper);
    this.usersService = usersService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final Router usersRouter = Router.router(vertx);

    usersRouter.route().handler(BodyHandler.create());

    usersRouter.post().handler(this::create);

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
