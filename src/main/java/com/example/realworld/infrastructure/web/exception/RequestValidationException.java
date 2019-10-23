package com.example.realworld.infrastructure.web.exception;

import com.example.realworld.infrastructure.web.model.response.ErrorResponse;

public class RequestValidationException extends RuntimeException {

  private ErrorResponse errorResponse;

  public RequestValidationException(ErrorResponse errorResponse) {
    this.errorResponse = errorResponse;
  }

  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }
}
