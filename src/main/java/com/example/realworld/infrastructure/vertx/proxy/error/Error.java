package com.example.realworld.infrastructure.vertx.proxy.error;

import com.example.realworld.domain.user.exception.BusinessException;

public class Error<E extends BusinessException> {

  private String className;
  private E exception;

  public Error() {}

  public Error(String className, E exception) {
    this.className = className;
    this.exception = exception;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public E getException() {
    return exception;
  }

  public void setException(E exception) {
    this.exception = exception;
  }
}
