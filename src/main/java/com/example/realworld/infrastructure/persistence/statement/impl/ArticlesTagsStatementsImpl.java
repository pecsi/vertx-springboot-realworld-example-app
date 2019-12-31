package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.infrastructure.persistence.statement.ArticlesTagsStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

@Component
public class ArticlesTagsStatementsImpl implements ArticlesTagsStatements {
  @Override
  public Statement<JsonArray> findTagsByArticle(String articleId) {

    String sql =
        "SELECT tags.ID, tags.NAME FROM ARTICLES_TAGS articles_tags "
            + "INNER JOIN TAGS tags on articles_tags.TAG_ID = tags.ID "
            + "WHERE articles_tags.ARTICLE_ID = ?";

    JsonArray params = new JsonArray().add(articleId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> store(String tagId, String articleId) {

    String sql = "INSERT INTO ARTICLES_TAGS (TAG_ID, ARTICLE_ID) VALUES (?, ?)";

    JsonArray params = new JsonArray().add(tagId).add(articleId);

    return new JsonArrayStatement(sql, params);
  }
}
