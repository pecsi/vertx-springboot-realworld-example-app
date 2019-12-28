package com.example.realworld.domain.user.exception;

public class UserAlreadyFollowedException extends BusinessException {
  public UserAlreadyFollowedException() {
    super("user already followed");
  }
}
