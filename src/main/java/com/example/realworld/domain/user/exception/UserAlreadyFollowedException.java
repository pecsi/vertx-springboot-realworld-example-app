package com.example.realworld.domain.user.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class UserAlreadyFollowedException extends BusinessException {
  public UserAlreadyFollowedException() {
    super("user already followed");
  }
}
