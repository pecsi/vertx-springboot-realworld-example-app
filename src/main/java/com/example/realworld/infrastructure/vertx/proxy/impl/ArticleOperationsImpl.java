package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.infrastructure.vertx.proxy.ArticleOperations;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class ArticleOperationsImpl extends AbstractOperations implements ArticleOperations {

  private ArticleService articleService;

  public ArticleOperationsImpl(ArticleService articleService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.articleService = articleService;
  }

  @Override
  public void findRecentArticles(
      String currentUserId, int offset, int limit, Handler<AsyncResult<ArticlesResponse>> handler) {
    articleService
        .findRecentArticles(currentUserId, offset, limit)
        .flatMap(
            articles ->
                articleService
                    .totalUserArticlesFollowed(currentUserId)
                    .map(articlesCount -> new ArticlesResponse(articles, articlesCount)))
        .subscribe(
            articlesResponse -> handler.handle(Future.succeededFuture(articlesResponse)),
            throwable -> handler.handle(error(throwable)));
  }
}
