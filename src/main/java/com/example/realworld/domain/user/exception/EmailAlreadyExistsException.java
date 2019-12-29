package com.example.realworld.domain.user.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {

  public EmailAlreadyExistsException() {
    super("email already exists");
  }
}
