package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.user.model.User;
import com.example.realworld.infrastructure.persistence.statement.ArticleStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

import java.util.List;

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
  public Statement<JsonArray> store(Article article, User author) {

    String sql =
        "INSERT INTO ARTICLES (ID, TITLE, DESCRIPTION, BODY, SLUG, AUTHOR_ID, CREATED_AT, UPDATED_AT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    JsonArray params =
        new JsonArray()
            .add(article.getId())
            .add(article.getTitle())
            .add(article.getDescription())
            .add(article.getBody())
            .add(article.getSlug())
            .add(author.getId())
            .add(ParserUtils.toTimestamp(article.getCreatedAt()))
            .add(ParserUtils.toTimestamp(article.getUpdatedAt()));

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> findArticles(
      int offset, int limit, List<String> tags, List<String> authors, List<String> favorited) {

    String sql =
        "SELECT articles.ID, "
            + "articles.TITLE, "
            + "articles.DESCRIPTION, "
            + "articles.BODY, "
            + "articles.SLUG, "
            + "articles.CREATED_AT, "
            + "articles.UPDATED_AT, "
            + "users.USERNAME AS AUTHOR_USERNAME "
            + "FROM ARTICLES articles"
            + "INNER JOIN USERS users ON articles.AUTHOR_ID = users.ID ";

    JsonArray params = new JsonArray();

    boolean tagsINotEmpty = !tags.isEmpty();
    boolean favoritedIsNotEmpty = !favorited.isEmpty();
    boolean authorsINotEmpty = !authors.isEmpty();

    if (tagsINotEmpty) {
      sql += "INNER JOIN ARTICLES_TAGS articles_tags ON articles.ID = articles_tags.ARTICLE_ID ";
      tags.forEach(params::add);
    }

    if (favoritedIsNotEmpty) {
      sql += "INNER JOIN ARTICLES_USERS articles_users ON articles.ID = articles_users.ARTICLE_ID ";
      favorited.forEach(params::add);
    }

    if (tagsINotEmpty || favoritedIsNotEmpty || authorsINotEmpty) {
      sql += "WHERE ";
    }

    if (authorsINotEmpty) {
      sql += "users.USERNAME";
    }

    return null;
  }
}
