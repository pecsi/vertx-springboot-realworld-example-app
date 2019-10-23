package com.example.realworld.domain.exception;

public class BusinessException extends RuntimeException {

  public BusinessException() {}

  public BusinessException(String message) {
    super(message);
  }
}
