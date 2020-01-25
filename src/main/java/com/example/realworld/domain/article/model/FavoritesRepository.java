package com.example.realworld.domain.article.model;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface FavoritesRepository {
  Single<Long> countByArticleIdAndUserId(String articleId, String userId);

  Single<Long> countByArticleId(String articleId);

  Completable deleteByArticle(String articleId);

  Completable store(String articleId, String authorId);

  Completable deleteByArticleAndAuthor(String articleId, String authorId);
}
