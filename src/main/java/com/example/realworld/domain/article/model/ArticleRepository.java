package com.example.realworld.domain.article.model;

import io.reactivex.Single;

import java.util.List;

public interface ArticleRepository {
  Single<Long> countBySlug(String slug);

  Single<Article> store(Article article);

  Single<List<Article>> findArticles(
      int offset, int limit, List<String> tags, List<String> authors, List<String> favorited);

  Single<Long> totalArticles(List<String> tags, List<String> authors, List<String> favorited);
}
