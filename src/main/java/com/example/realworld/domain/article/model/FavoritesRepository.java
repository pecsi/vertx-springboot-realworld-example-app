package com.example.realworld.domain.article.model;

import io.reactivex.Single;

public interface FavoritesRepository {
  Single<Long> countByArticleIdAndUserId(String articleId, String userId);

  Single<Long> countByArticleId(String articleId);
}
