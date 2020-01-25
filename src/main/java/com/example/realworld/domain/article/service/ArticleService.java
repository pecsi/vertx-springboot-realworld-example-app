package com.example.realworld.domain.article.service;

import com.example.realworld.application.data.ArticleData;
import com.example.realworld.application.data.ArticlesData;
import com.example.realworld.application.data.CommentData;
import com.example.realworld.domain.article.model.NewArticle;
import com.example.realworld.domain.article.model.UpdateArticle;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

public interface ArticleService {

  Single<ArticlesData> findRecentArticles(String currentUserId, int offset, int limit);

  Single<ArticleData> create(String currentUserId, NewArticle newArticle);

  Single<Long> totalUserArticlesFollowed(String currentUserId);

  Single<ArticlesData> findArticles(
      String currentUserId,
      int offset,
      int limit,
      List<String> tags,
      List<String> authors,
      List<String> favorited);

  Single<Long> totalArticles(List<String> tags, List<String> authors, List<String> favorited);

  Single<ArticleData> findBySlug(String slug, String currentUserId);

  Single<ArticleData> updateBySlug(String slug, String currentUserId, UpdateArticle updateArticle);

  Completable deleteArticleBySlugAndAuthorId(String slug, String currentUserId);

  Single<CommentData> createCommentBySlug(String slug, String currentUserId, String commentBody);

  Completable deleteCommentByIdAndAuthorId(String commentId, String currentUserId);

  Single<List<CommentData>> findCommentsBySlug(String slug, String currentUserId);

  Single<ArticleData> favoriteArticle(String slug, String currentUserId);

  Single<ArticleData> unfavoriteArticle(String slug, String currentUserId);
}
