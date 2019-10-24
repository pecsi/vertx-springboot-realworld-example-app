package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.service.error.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.serviceproxy.ServiceException;

class AbstractService {

  ObjectMapper objectMapper;

  AbstractService(ObjectMapper objectMapper) {
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
