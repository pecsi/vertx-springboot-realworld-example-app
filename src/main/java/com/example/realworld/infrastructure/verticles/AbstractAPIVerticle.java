package com.example.realworld.infrastructure.verticles;

import com.example.realworld.infrastructure.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

public class AbstractAPIVerticle extends AbstractVerticle {

  private ObjectMapper objectMapper;
  private Validator validator;

  public AbstractAPIVerticle(ObjectMapper objectMapper, Validator validator) {
    this.validator = validator;
    this.objectMapper = objectMapper;
  }

  protected void createHttpServer(
      Handler<HttpServerRequest> httpServerRequestHandler,
      Handler<AsyncResult<HttpServer>> handler) {

    vertx
        .createHttpServer()
        .requestHandler(httpServerRequestHandler)
        .listen(config().getInteger(Constants.SERVER_PORT_KEY, 8080), handler);
  }

  protected <T> T getBody(RoutingContext routingContext, Class<T> clazz) {
    T result = null;
    try {
      result = objectMapper.readValue(routingContext.getBodyAsString(), clazz);
    } catch (IOException ex) {
      routingContext.fail(ex);
    }
    return result;
  }

  protected <T> T getBodyAndValid(RoutingContext routingContext, Class<T> clazz) {
    T result = getBody(routingContext, clazz);
    Set<ConstraintViolation<T>> violations = validator.validate(result);
    return result;
  }

  protected <T> void response(RoutingContext routingContext, int statusCode, T response) {
    try {
      routingContext
          .response()
          .setStatusCode(statusCode)
          .end(objectMapper.writeValueAsString(response));
    } catch (JsonProcessingException e) {
      routingContext.fail(e);
    }
  }
}
