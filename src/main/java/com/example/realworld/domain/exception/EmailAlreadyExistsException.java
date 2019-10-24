package com.example.realworld.domain.exception;

public class EmailAlreadyExistsException extends BusinessException {

  public EmailAlreadyExistsException() {
    super("email already exists");
  }
}
