package com.example.realworld.domain.tag.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class TagAlreadyExistsException extends BusinessException {

  public TagAlreadyExistsException() {
    super("tag already exists");
  }
}
