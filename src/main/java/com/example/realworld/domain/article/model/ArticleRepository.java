package com.example.realworld.domain.article.model;

import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
  Single<Long> countBySlug(String slug);

  Single<Long> countBySlug(String slug, String excludeArticleId);

  Single<Article> store(Article article);

  Single<List<Article>> findArticles(
      int offset, int limit, List<String> tags, List<String> authors, List<String> favorited);

  Single<Long> totalArticles(List<String> tags, List<String> authors, List<String> favorited);

  Single<Optional<Article>> findBySlug(String slug);

  Single<Article> update(Article article);

  Completable deleteByArticleIdAndAuthorId(String articleId, String authorId);

  Single<Optional<Article>> findBySlugAndAuthorId(String slug, String authorId);
}
