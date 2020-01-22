package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.infrastructure.persistence.statement.ArticlesUsersStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

@Component
public class ArticlesUsersStatementsImpl extends AbstractStatements
    implements ArticlesUsersStatements {
  @Override
  public Statement<JsonArray> countByArticleIdAndUserId(String articleId, String userId) {

    String sql = "SELECT COUNT(*) FROM ARTICLES_USERS WHERE ARTICLE_ID = ? AND USER_ID = ?";

    JsonArray params = new JsonArray().add(articleId).add(userId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> countByArticleId(String articleId) {

    String sql = "SELECT COUNT(*) FROM ARTICLES_USERS WHERE ARTICLE_ID = ?";

    JsonArray params = new JsonArray().add(articleId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> deleteByArticle(String articleId) {

    String sql = "DELETE FROM ARTICLES_USERS WHERE ARTICLE_ID = ?";

    JsonArray params = new JsonArray().add(articleId);

    return new JsonArrayStatement(sql, params);
  }
}
