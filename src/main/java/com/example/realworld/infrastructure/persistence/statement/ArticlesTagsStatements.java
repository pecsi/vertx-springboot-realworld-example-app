package com.example.realworld.infrastructure.persistence.statement;

import io.vertx.core.json.JsonArray;

public interface ArticlesTagsStatements {
  Statement<JsonArray> findTagsByArticle(String articleId);

  Statement<JsonArray> store(String tagId, String articleId);
}
