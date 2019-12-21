package com.example.realworld.infrastructure.vertx.verticle;

import com.example.realworld.infrastructure.vertx.exception.mapper.BusinessExceptionMapper;
import com.example.realworld.infrastructure.web.exception.RequestValidationException;
import com.example.realworld.infrastructure.web.model.response.ErrorResponse;
import com.example.realworld.infrastructure.web.route.HttpRoute;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.serviceproxy.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class AbstractHttpVerticle extends AbstractVerticle {

  final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  @Qualifier("wrapUnwrapRootValueObjectMapper")
  private ObjectMapper wrapUnwrapRootValueObjectMapper;

  @Autowired private BusinessExceptionMapper businessExceptionMapper;

  @Value("${vertx.server.context_path}")
  private String contextPath;

  @Value("${vertx.server.port}")
  private int serverPort;

  protected void createHttpServer(List<HttpRoute> httpRoutes, Promise<Void> startPromise) {
    vertx
        .createHttpServer()
        .requestHandler(subRouter(httpRoutes))
        .rxListen(serverPort)
        .subscribe(
            httpServer -> {
              logger.info("HttpVerticle started on port " + serverPort);
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
