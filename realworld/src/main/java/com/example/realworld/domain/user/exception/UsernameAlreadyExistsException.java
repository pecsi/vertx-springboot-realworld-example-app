package com.example.realworld.domain.user.exception;

public class UsernameAlreadyExistsException extends BusinessException {

  public UsernameAlreadyExistsException() {
    super("username already exists");
  }
}
