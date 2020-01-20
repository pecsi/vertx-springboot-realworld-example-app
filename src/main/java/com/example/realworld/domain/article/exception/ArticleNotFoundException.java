package com.example.realworld.domain.article.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class ArticleNotFoundException extends BusinessException {

  public ArticleNotFoundException() {
    super("article not found");
  }
}
