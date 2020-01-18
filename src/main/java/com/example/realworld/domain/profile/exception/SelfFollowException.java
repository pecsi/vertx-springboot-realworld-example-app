package com.example.realworld.domain.profile.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class SelfFollowException extends BusinessException {

  public SelfFollowException() {
    super("self follow is not allowed");
  }
}
