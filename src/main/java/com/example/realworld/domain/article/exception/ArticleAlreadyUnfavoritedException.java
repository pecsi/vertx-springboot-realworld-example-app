package com.example.realworld.domain.article.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class ArticleAlreadyUnfavoritedException extends BusinessException {

  public ArticleAlreadyUnfavoritedException() {
    super("article already unfavorited");
  }
}
