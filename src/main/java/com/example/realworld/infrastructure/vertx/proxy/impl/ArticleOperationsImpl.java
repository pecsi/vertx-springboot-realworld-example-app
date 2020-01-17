package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.infrastructure.vertx.proxy.ArticleOperations;
import com.example.realworld.infrastructure.web.model.response.ArticlesFeedResponse;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.List;

public class ArticleOperationsImpl extends AbstractOperations implements ArticleOperations {

  private ArticleService articleService;

  public ArticleOperationsImpl(ArticleService articleService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.articleService = articleService;
  }

  @Override
  public void findRecentArticles(
      String currentUserId,
      int offset,
      int limit,
      Handler<AsyncResult<ArticlesFeedResponse>> handler) {
    articleService
        .findRecentArticles(currentUserId, offset, limit)
        .subscribe(
            articlesData ->
                handler.handle(Future.succeededFuture(new ArticlesFeedResponse(articlesData))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void findArticles(
      String currentUserId,
      int offset,
      int limit,
      List<String> tags,
      List<String> authors,
      List<String> favorited,
      Handler<AsyncResult<ArticlesResponse>> handler) {
    articleService.findArticles(currentUserId, offset, limit, tags, authors, favorited);
  }
}
