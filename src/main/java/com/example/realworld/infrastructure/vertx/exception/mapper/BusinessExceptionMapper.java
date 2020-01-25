package com.example.realworld.infrastructure.vertx.exception.mapper;

import com.example.realworld.domain.article.exception.ArticleAlreadyFavoritedException;
import com.example.realworld.domain.general.exception.BusinessException;
import com.example.realworld.domain.profile.exception.SelfFollowException;
import com.example.realworld.domain.user.exception.EmailAlreadyExistsException;
import com.example.realworld.domain.user.exception.InvalidLoginException;
import com.example.realworld.domain.user.exception.UserNotFoundException;
import com.example.realworld.domain.user.exception.UsernameAlreadyExistsException;
import com.example.realworld.infrastructure.vertx.proxy.error.Error;
import com.example.realworld.infrastructure.web.model.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.serviceproxy.ServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BusinessExceptionMapper {

  private Map<String, BusinessExceptionHandler> exceptionMapper;
  private ObjectMapper wrapUnwrapRootValueObjectMapper;
  private ObjectMapper defaultObjectMapper;

  public BusinessExceptionMapper(
      @Qualifier("wrapUnwrapRootValueObjectMapper") ObjectMapper wrapUnwrapRootValueObjectMapper,
      @Qualifier("defaultObjectMapper") ObjectMapper defaultObjectMapper) {
    this.wrapUnwrapRootValueObjectMapper = wrapUnwrapRootValueObjectMapper;
    this.exceptionMapper = configureExceptionMapper();
    this.defaultObjectMapper = defaultObjectMapper;
  }

  private Map<String, BusinessExceptionHandler> configureExceptionMapper() {

    Map<String, BusinessExceptionHandler> handlerMap = new HashMap<>();

    handlerMap.put(UsernameAlreadyExistsException.class.getName(), conflict());
    handlerMap.put(EmailAlreadyExistsException.class.getName(), conflict());
    handlerMap.put(InvalidLoginException.class.getName(), unauthorized());
    handlerMap.put(UserNotFoundException.class.getName(), notFound());
    handlerMap.put(SelfFollowException.class.getName(), conflict());
    handlerMap.put(ArticleAlreadyFavoritedException.class.getName(), conflict());
    //    handlerMap.put(ResourceNotFoundException.class, notFound());
    //    handlerMap.put(TagNotFoundException.class, notFound());
    //    handlerMap.put(ArticleNotFoundException.class, notFound());

    return handlerMap;
  }

  private BusinessExceptionHandler notFound() {
    return exceptionHandler(
        HttpResponseStatus.NOT_FOUND.reasonPhrase(), HttpResponseStatus.NOT_FOUND.code());
  }

  private BusinessExceptionHandler conflict() {
    return exceptionHandler(
        HttpResponseStatus.CONFLICT.reasonPhrase(), HttpResponseStatus.CONFLICT.code());
  }

  private BusinessExceptionHandler unauthorized() {
    return exceptionHandler(
        HttpResponseStatus.UNAUTHORIZED.reasonPhrase(), HttpResponseStatus.UNAUTHORIZED.code());
  }

  private BusinessExceptionHandler exceptionHandler(String message, int httpStatusCode) {
    return (httpServerResponse, businessException) -> {
      ErrorResponse errorResponse = new ErrorResponse(message);
      if (businessException.haveMessages()) {
        errorResponse = new ErrorResponse(businessException.getMessages());
      }
      errorResponse(httpServerResponse, errorResponse, httpStatusCode);
    };
  }

  private void errorResponse(
      HttpServerResponse httpServerResponse, ErrorResponse errorResponse, int httpStatusCode) {
    try {
      httpServerResponse
          .setStatusCode(httpStatusCode)
          .end(wrapUnwrapRootValueObjectMapper.writeValueAsString(errorResponse));
    } catch (JsonProcessingException e) {
      httpServerResponse.end(e.getMessage());
    }
  }

  public void handle(ServiceException serviceException, HttpServerResponse httpServerResponse) {
    try {
      Error error = defaultObjectMapper.readValue(serviceException.getMessage(), Error.class);
      this.exceptionMapper
          .get(error.getClassName())
          .handler(httpServerResponse, error.getException());
    } catch (Exception e) {
      httpServerResponse.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
    }
  }

  private interface BusinessExceptionHandler {
    void handler(HttpServerResponse httpServerResponse, BusinessException businessException);
  }
}
