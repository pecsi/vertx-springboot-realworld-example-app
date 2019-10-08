package com.example.realworld.infrastructure.verticles;

import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.exceptions.RequestValidationException;
import com.example.realworld.infrastructure.web.model.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
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
    T result;
    try {
      result = objectMapper.readValue(routingContext.getBodyAsString(), clazz);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  protected <T> T getBodyAndValid(RoutingContext routingContext, Class<T> clazz) {
    T result = getBody(routingContext, clazz);
    validateRequestBody(result);
    return result;
  }

  private void handlerRequestValidation(
      HttpServerResponse httpServerResponse,
      RequestValidationException requestValidationException) {

    httpServerResponse
        .setStatusCode(HttpResponseStatus.UNPROCESSABLE_ENTITY.code())
        .end(writeValueAsString(requestValidationException.getErrorResponse()));
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
          .end(objectMapper.writeValueAsString(response));
    } catch (JsonProcessingException e) {
      routingContext.fail(e);
    }
  }

  protected Router subRouter(Router router) {
    final Router baseRouter = Router.router(vertx);
    configApiErrorHandler(baseRouter);
    String contextPath = config().getString("context_path");
    return baseRouter.mountSubRouter(contextPath, router);
  }

  private void configApiErrorHandler(Router baseRouter) {
    baseRouter
        .route()
        .failureHandler(
            failureRoutingContext -> {
              HttpServerResponse response = failureRoutingContext.response();

              if (failureRoutingContext.failure() instanceof RequestValidationException) {

                handlerRequestValidation(
                    response, (RequestValidationException) failureRoutingContext.failure());

              } else {

                response.end(
                    writeValueAsString(
                        new ErrorResponse(failureRoutingContext.failure().getMessage())));
              }
            });
  }

  protected String writeValueAsString(Object value) {
    String result;
    try {
      result = objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }
}
