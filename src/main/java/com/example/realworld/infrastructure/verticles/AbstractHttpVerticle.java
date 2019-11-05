package com.example.realworld.infrastructure.verticles;

import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.context.annotation.WrapUnwrapRootValueObjectMapper;
import com.example.realworld.infrastructure.web.exception.RequestValidationException;
import com.example.realworld.infrastructure.web.exception.mapper.BusinessExceptionMapper;
import com.example.realworld.infrastructure.web.model.response.ErrorResponse;
import com.example.realworld.infrastructure.web.route.HttpRoute;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.serviceproxy.ServiceException;

import java.util.List;

public class AbstractHttpVerticle extends AbstractVerticle {

  final Logger logger = LoggerFactory.getLogger(getClass());

  @Inject @WrapUnwrapRootValueObjectMapper private ObjectMapper wrapUnwrapRootValueObjectMapper;
  @Inject private BusinessExceptionMapper businessExceptionMapper;

  protected void createHttpServer(List<HttpRoute> httpRoutes, Promise<Void> startPromise) {
    vertx
        .createHttpServer()
        .requestHandler(subRouter(httpRoutes))
        .rxListen(config().getInteger(Constants.SERVER_PORT_KEY, 8080))
        .subscribe(
            httpServer -> {
              logger.info(
                  "HttpVerticle started on port " + config().getInteger(Constants.SERVER_PORT_KEY));
              startPromise.complete();
            },
            startPromise::fail);
  }

  private void handlerRequestValidation(
      HttpServerResponse httpServerResponse,
      RequestValidationException requestValidationException) {

    httpServerResponse
        .setStatusCode(HttpResponseStatus.UNPROCESSABLE_ENTITY.code())
        .end(writeValueAsString(requestValidationException.getErrorResponse()));
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

  protected Router subRouter(List<HttpRoute> routers) {
    final Router baseRouter = Router.router(vertx);
    configApiErrorHandler(baseRouter);
    String contextPath = config().getString(Constants.CONTEXT_PATH_KEY);
    routers.forEach(router -> baseRouter.mountSubRouter(contextPath, router.configure(vertx)));
    return baseRouter;
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

              } else if (failureRoutingContext.failure() instanceof ServiceException) {

                ServiceException serviceException =
                    (ServiceException) failureRoutingContext.failure();

                this.businessExceptionMapper.handle(serviceException, response);

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
      result = wrapUnwrapRootValueObjectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }
}
