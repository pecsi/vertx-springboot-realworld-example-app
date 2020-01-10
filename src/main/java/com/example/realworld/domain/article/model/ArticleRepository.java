package com.example.realworld.domain.article.model;

import com.example.realworld.domain.user.model.User;
import io.reactivex.Single;

import java.util.List;

public interface ArticleRepository {
  Single<Long> countBySlug(String slug);

  Single<Article> store(Article article, User author);

  Single<List<Article>> findArticles(
      int offset, int limit, List<String> tags, List<String> authors, List<String> favorited);
}
