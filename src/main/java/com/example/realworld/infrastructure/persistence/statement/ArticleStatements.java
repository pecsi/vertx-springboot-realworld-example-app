package com.example.realworld.infrastructure.persistence.statement;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.user.model.User;
import io.vertx.core.json.JsonArray;

import java.util.List;

public interface ArticleStatements {
  Statement<JsonArray> countBy(String field, String value);

  Statement<JsonArray> store(Article article, User author);

  Statement<JsonArray> findArticles(
      int offset, int limit, List<String> tags, List<String> authors, List<String> favorited);
}
