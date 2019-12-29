package com.example.realworld.domain.article.model;

import io.reactivex.Single;

public interface ArticleRepository {
  Single<Long> countBySlug(String slug);

  Single<Article> store(Article article);
}
