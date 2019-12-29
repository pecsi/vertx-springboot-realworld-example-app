package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.infrastructure.persistence.statement.ArticleStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

@Component
public class ArticleStatementsImpl implements ArticleStatements {
  @Override
  public Statement<JsonArray> countBy(String field, String value) {
    String sql =
        String.format("SELECT COUNT(*) FROM ARTICLES WHERE UPPER(%s) = ?", field.toUpperCase());
    JsonArray params = new JsonArray().add(value.toUpperCase().trim());
    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> store(Article article) {

    String sql =
        "INSERT INTO ARTICLES (ID, TITLE, DESCRIPTION, BODY, SLUG, AUTHOR_ID, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    JsonArray params =
        new JsonArray()
            .add(article.getId())
            .add(article.getTitle())
            .add(article.getDescription())
            .add(article.getBody())
            .add(article.getSlug())
            .add(article.getAuthor().getId())
            .add(ParserUtils.toTimestamp(article.getCreatedAt()))
            .add(ParserUtils.toTimestamp(article.getUpdatedAt()));

    return new JsonArrayStatement(sql, params);
  }
}
