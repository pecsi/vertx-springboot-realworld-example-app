package com.example.realworld.domain.article.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class SlugAlreadyExistsException extends BusinessException {

  public SlugAlreadyExistsException() {
    super("slug already exists");
  }
}
