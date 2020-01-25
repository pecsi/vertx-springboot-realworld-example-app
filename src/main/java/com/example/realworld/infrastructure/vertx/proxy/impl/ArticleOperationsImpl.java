package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.infrastructure.vertx.proxy.ArticleOperations;
import com.example.realworld.infrastructure.web.model.request.NewArticleRequest;
import com.example.realworld.infrastructure.web.model.request.NewCommentRequest;
import com.example.realworld.infrastructure.web.model.request.UpdateArticleRequest;
import com.example.realworld.infrastructure.web.model.response.ArticleResponse;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import com.example.realworld.infrastructure.web.model.response.CommentResponse;
import com.example.realworld.infrastructure.web.model.response.CommentsResponse;
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
      String currentUserId, int offset, int limit, Handler<AsyncResult<ArticlesResponse>> handler) {
    articleService
        .findRecentArticles(currentUserId, offset, limit)
        .subscribe(
            articlesData ->
                handler.handle(Future.succeededFuture(new ArticlesResponse(articlesData))),
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
    articleService
        .findArticles(currentUserId, offset, limit, tags, authors, favorited)
        .subscribe(
            articlesData ->
                handler.handle(Future.succeededFuture(new ArticlesResponse(articlesData))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void create(
      String currentUserId,
      NewArticleRequest newArticleRequest,
      Handler<AsyncResult<ArticleResponse>> handler) {
    articleService
        .create(currentUserId, newArticleRequest.toNewArticle())
        .subscribe(
            articleData -> handler.handle(Future.succeededFuture(new ArticleResponse(articleData))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void findBySlug(
      String slug, String currentUserId, Handler<AsyncResult<ArticleResponse>> handler) {
    articleService
        .findBySlug(slug, currentUserId)
        .subscribe(
            articleData -> handler.handle(Future.succeededFuture(new ArticleResponse(articleData))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void updateBySlug(
      String slug,
      String currentUserId,
      UpdateArticleRequest updateArticleRequest,
      Handler<AsyncResult<ArticleResponse>> handler) {
    articleService
        .updateBySlug(slug, currentUserId, updateArticleRequest.toUpdateArticle())
        .subscribe(
            articleData -> handler.handle(Future.succeededFuture(new ArticleResponse(articleData))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void deleteArticleBySlug(
      String slug, String currentUserId, Handler<AsyncResult<Void>> handler) {
    articleService
        .deleteArticleBySlugAndAuthorId(slug, currentUserId)
        .subscribe(
            () -> handler.handle(Future.succeededFuture()),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void createCommentBySlug(
      String slug,
      String currentUserId,
      NewCommentRequest newCommentRequest,
      Handler<AsyncResult<CommentResponse>> handler) {
    articleService
        .createCommentBySlug(slug, currentUserId, newCommentRequest.getBody())
        .subscribe(
            commentData -> handler.handle(Future.succeededFuture(new CommentResponse(commentData))),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void deleteCommentByIdAndAuthorId(
      String commentId, String currentUserId, Handler<AsyncResult<Void>> handler) {
    articleService
        .deleteCommentByIdAndAuthorId(commentId, currentUserId)
        .subscribe(
            () -> handler.handle(Future.succeededFuture()),
            throwable -> handler.handle(error(throwable)));
  }

  @Override
  public void findCommentsBySlug(
      String slug, String currentUserId, Handler<AsyncResult<CommentsResponse>> handler) {
    articleService
        .findCommentsBySlug(slug, currentUserId)
        .subscribe(
            comments -> handler.handle(Future.succeededFuture(new CommentsResponse(comments))),
            throwable -> handler.handle(error(throwable)));
  }
}
