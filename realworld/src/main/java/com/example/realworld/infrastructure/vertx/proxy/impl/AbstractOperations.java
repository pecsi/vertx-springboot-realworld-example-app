package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.infrastructure.vertx.proxy.error.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.serviceproxy.ServiceException;

public class AbstractOperations {

  private ObjectMapper objectMapper;

  public AbstractOperations(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
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
