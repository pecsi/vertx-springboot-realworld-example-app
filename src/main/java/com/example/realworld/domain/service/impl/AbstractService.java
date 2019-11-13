package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.service.error.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.serviceproxy.ServiceException;

import java.util.function.Consumer;

class AbstractService {

  ObjectMapper objectMapper;

  AbstractService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  protected <T> Handler<AsyncResult<T>> result(Consumer<T> consumer) {
    return (AsyncResult<T> asyncResult) -> {
      if (asyncResult.succeeded()) {
        consumer.accept(asyncResult.result());
      } else {
        throw new RuntimeException(asyncResult.cause());
      }
    };
  }

  <T> AsyncResult<T> error(Throwable throwable) {
    String error;
    try {
      error =
          objectMapper.writeValueAsString(new Error<>(throwable.getClass().getName(), throwable));
    } catch (JsonProcessingException ex) {
      error = ex.getMessage();
    }
    return ServiceException.fail(1, error);
  }
}
