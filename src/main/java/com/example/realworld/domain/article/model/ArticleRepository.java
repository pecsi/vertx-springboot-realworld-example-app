package com.example.realworld.domain.article.model;

import com.example.realworld.domain.user.model.User;
import io.reactivex.Single;

public interface ArticleRepository {
  Single<Long> countBySlug(String slug);

  Single<Article> store(Article article, User author);
}
