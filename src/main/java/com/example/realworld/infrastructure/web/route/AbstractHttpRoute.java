package com.example.realworld.infrastructure.web.route;

import com.example.realworld.infrastructure.context.annotation.DefaultObjectMapper;
import com.example.realworld.infrastructure.context.annotation.WrapUnwrapRootValueObjectMapper;
import com.example.realworld.infrastructure.web.exception.RequestValidationException;
import com.example.realworld.infrastructure.web.model.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.RoutingContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractHttpRoute implements HttpRoute {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String AUTHORIZATION_HEADER_PREFIX = "Token ";
  public static final String USER_ID_CONTEXT_KEY = "userId";

  @Inject @WrapUnwrapRootValueObjectMapper private ObjectMapper wrapUnwrapRootValueObjectMapper;
  @Inject @DefaultObjectMapper private ObjectMapper defaultObjectMapper;
  @Inject private Validator validator;
  @Inject protected JWTAuth jwtAuth;

  protected void jwtHandler(RoutingContext routingContext, boolean optional) {

    String authorization = routingContext.request().headers().get(AUTHORIZATION_HEADER);

    if (authorization != null && authorization.contains(AUTHORIZATION_HEADER_PREFIX)) {

      String token = authorization.replace(AUTHORIZATION_HEADER_PREFIX, "");

      jwtAuth
          .rxAuthenticate(new JsonObject().put("jwt", token))
          .subscribe(
              user -> {
                routingContext.put(USER_ID_CONTEXT_KEY, user.principal().getLong("sub"));
                routingContext.next();
              },
              throwable -> optionalAuthorization(routingContext, optional));
    } else {
      optionalAuthorization(routingContext, optional);
    }
  }

  private void optionalAuthorization(RoutingContext routingContext, boolean optional) {
    if (optional) {
      routingContext.next();
    } else {
      unauthorizedResponse(routingContext);
    }
  }

  protected void unauthorizedResponse(RoutingContext routingContext) {
    response(
        routingContext, HttpResponseStatus.UNAUTHORIZED.code(), new ErrorResponse("Unauthorized"));
  }

  protected <T> Optional<T> userIdOptional(RoutingContext routingContext) {
    return Optional.ofNullable(routingContext.get(USER_ID_CONTEXT_KEY));
  }

  protected <T> void userId(RoutingContext routingContext, boolean optional, Consumer<T> consumer) {
    Optional<T> userIdOptional = userIdOptional(routingContext);

    if (userIdOptional.isPresent()) {
      consumer.accept(userIdOptional.get());
    } else {
      if (optional) {
        consumer.accept(null);
      } else {
        unauthorizedResponse(routingContext);
      }
    }
  }

  protected <T> T getBodyAndValid(RoutingContext routingContext, Class<T> clazz) {
    T result = getBody(routingContext, clazz);
    validateRequestBody(result);
    return result;
  }

  protected <T> T getBody(RoutingContext routingContext, Class<T> clazz) {
    T result;
    try {
      result = wrapUnwrapRootValueObjectMapper.readValue(routingContext.getBodyAsString(), clazz);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  private <T> void validateRequestBody(T body) {

    Set<ConstraintViolation<T>> violations = validator.validate(body);

    if (!violations.isEmpty()) {

      ErrorResponse errorResponse = new ErrorResponse();
      violations.forEach(constraint -> errorResponse.getBody().add(constraint.getMessage()));

      throw new RequestValidationException(errorResponse);
    }
  }

  protected <T> void response(RoutingContext routingContext, int statusCode, T response) {
    try {
      routingContext
          .response()
          .setStatusCode(statusCode)
          .end(wrapUnwrapRootValueObjectMapper.writeValueAsString(response));
    } catch (JsonProcessingException e) {
      routingContext.fail(e);
    }
  }

  protected <T> Handler<AsyncResult<T>> responseOrFail(
      RoutingContext routingContext, int statusCode, Function<T, Object> responseFunction) {
    return asyncResult -> {
      if (asyncResult.succeeded()) {
        T result = asyncResult.result();
        response(routingContext, statusCode, responseFunction.apply(result));
      } else {
        routingContext.fail(asyncResult.cause());
      }
    };
  }
}
