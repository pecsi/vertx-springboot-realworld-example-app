package com.example.realworld.domain.user.exception;

public class EmailAlreadyExistsException extends BusinessException {

  public EmailAlreadyExistsException() {
    super("email already exists");
  }
}
