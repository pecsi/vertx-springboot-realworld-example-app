package com.example.realworld.domain.tag.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class TagNotFoundException extends BusinessException {

  public TagNotFoundException() {
    super("tag not found");
  }
}
