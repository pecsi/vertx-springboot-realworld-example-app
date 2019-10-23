package com.example.realworld.domain.service.error;

public class Error<E extends Throwable> {

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
