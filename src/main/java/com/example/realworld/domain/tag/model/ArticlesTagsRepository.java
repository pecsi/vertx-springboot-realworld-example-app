package com.example.realworld.domain.tag.model;

import com.example.realworld.domain.article.model.Article;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

public interface ArticlesTagsRepository {
  Single<List<Tag>> findTagsByArticle(String articleId);

  Completable tagArticle(Tag tag, Article article);

  Completable deleteByArticle(String articleId);
}
