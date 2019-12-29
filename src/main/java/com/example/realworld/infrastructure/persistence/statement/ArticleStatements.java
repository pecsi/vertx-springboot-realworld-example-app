package com.example.realworld.infrastructure.persistence.statement;

import com.example.realworld.domain.article.model.Article;
import io.vertx.core.json.JsonArray;

public interface ArticleStatements {
  Statement<JsonArray> countBy(String field, String value);

  Statement<JsonArray> store(Article article);
}
