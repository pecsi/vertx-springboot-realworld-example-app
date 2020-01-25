package com.example.realworld.domain.article.exception;

import com.example.realworld.domain.general.exception.BusinessException;

public class ArticleAlreadyFavoritedException extends BusinessException {

  public ArticleAlreadyFavoritedException() {
    super("article already favorited");
  }
}
