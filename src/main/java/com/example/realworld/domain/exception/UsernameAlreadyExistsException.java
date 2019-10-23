package com.example.realworld.domain.exception;

public class UsernameAlreadyExistsException extends BusinessException {

  public UsernameAlreadyExistsException() {
    super("username already exists");
  }
}
