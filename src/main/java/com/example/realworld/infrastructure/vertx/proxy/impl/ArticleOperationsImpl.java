package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.domain.profile.service.ProfileService;
import com.example.realworld.infrastructure.vertx.proxy.ArticleOperations;
import com.example.realworld.infrastructure.web.model.response.ArticleResponse;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class ArticleOperationsImpl extends AbstractOperations implements ArticleOperations {

  private ArticleService articleService;
  private ProfileService profileService;

  public ArticleOperationsImpl(
      ArticleService articleService, ProfileService profileService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.articleService = articleService;
    this.profileService = profileService;
  }

  @Override
  public void findRecentArticles(
      String currentUserId, int offset, int limit, Handler<AsyncResult<ArticlesResponse>> handler) {

    articleService
        .findRecentArticles(currentUserId, offset, limit)
        .flattenAsFlowable(articles -> articles)
        .flatMapSingle(
            article ->
                profileService
                    .getProfile(article.getAuthor().getUsername(), currentUserId)
                    .map(profile -> new ArticleResponse(article, profile)))
        .toList()
        .flatMap(
            articleResponses ->
                articleService
                    .totalUserArticlesFollowed(currentUserId)
                    .map(articlesCount -> new ArticlesResponse(articleResponses, articlesCount)))
        .subscribe(
            articlesResponse -> handler.handle(Future.succeededFuture(articlesResponse)),
            throwable -> handler.handle(error(throwable)));
  }
}
