package com.example.realworld.domain.user.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class UsernameAlreadyExistsException extends BusinessException {

  public UsernameAlreadyExistsException() {
    super("username already exists");
  }
}
