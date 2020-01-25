package com.example.realworld.infrastructure.vertx.proxy;

import com.example.realworld.infrastructure.web.model.request.NewArticleRequest;
import com.example.realworld.infrastructure.web.model.request.NewCommentRequest;
import com.example.realworld.infrastructure.web.model.request.UpdateArticleRequest;
import com.example.realworld.infrastructure.web.model.response.ArticleResponse;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import com.example.realworld.infrastructure.web.model.response.CommentResponse;
import com.example.realworld.infrastructure.web.model.response.CommentsResponse;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

@ProxyGen
@VertxGen
public interface ArticleOperations {

  String SERVICE_ADDRESS = "articles-service-event-bus";

  void findRecentArticles(
      String currentUserId, int offset, int limit, Handler<AsyncResult<ArticlesResponse>> handler);

  void findArticles(
      String currentUserId,
      int offset,
      int limit,
      List<String> tags,
      List<String> authors,
      List<String> favorited,
      Handler<AsyncResult<ArticlesResponse>> handler);

  void create(
      String currentUserId,
      NewArticleRequest newArticleRequest,
      Handler<AsyncResult<ArticleResponse>> handler);

  void findBySlug(String slug, String currentUserId, Handler<AsyncResult<ArticleResponse>> handler);

  void updateBySlug(
      String slug,
      String currentUserId,
      UpdateArticleRequest updateArticleRequest,
      Handler<AsyncResult<ArticleResponse>> handler);

  void deleteArticleBySlug(String slug, String currentUserId, Handler<AsyncResult<Void>> handler);

  void createCommentBySlug(
      String slug,
      String currentUserId,
      NewCommentRequest newCommentRequest,
      Handler<AsyncResult<CommentResponse>> handler);

  void deleteCommentByIdAndAuthorId(
      String commentId, String currentUserId, Handler<AsyncResult<Void>> handler);

  void findCommentsBySlug(
      String slug, String currentUserId, Handler<AsyncResult<CommentsResponse>> handler);

  void favoriteArticle(
      String slug, String currentUserId, Handler<AsyncResult<ArticleResponse>> handler);

  void unfavoriteArticle(
      String slug, String currentUserId, Handler<AsyncResult<ArticleResponse>> handler);
}
