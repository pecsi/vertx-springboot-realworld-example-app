package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.infrastructure.persistence.statement.ArticleStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import com.example.realworld.infrastructure.utils.SimpleQueryBuilder;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
            + "users.ID AS AUTHOR_ID, "
            + "users.USERNAME AS AUTHOR_USERNAME "
            + "FROM ARTICLES articles "
            + "INNER JOIN USERS users ON articles.AUTHOR_ID = users.ID";

    JsonArray params = new JsonArray();

    SimpleQueryBuilder findArticlesQueryBuilder = new SimpleQueryBuilder();

    findArticlesQueryBuilder.addQueryStatement(sql);

    configQueryBuilderFindArticles(tags, authors, favorited, params, findArticlesQueryBuilder);

    findArticlesQueryBuilder.addAfterWhereStatement(
        "ORDER BY articles.CREATED_AT DESC, articles.UPDATED_AT DESC LIMIT ? OFFSET ?;");

    params.add(limit).add(offset);

    return new JsonArrayStatement(findArticlesQueryBuilder.toQueryString(), params);
  }

  private void configQueryBuilderFindArticles(
      List<String> tags,
      List<String> authors,
      List<String> favorited,
      JsonArray params,
      SimpleQueryBuilder findArticlesQueryBuilder) {

    if (!tags.isEmpty()) {
      String tagsQueryStatement =
          "INNER JOIN ARTICLES_TAGS articles_tags ON articles.ID = articles_tags.ARTICLE_ID "
              + "INNER JOIN TAGS tags ON articles_tags.TAG_ID = tags.ID";
      String tagsWhereStatement = String.format("UPPER(tags.NAME) IN (%s)", listParams(tags));
      findArticlesQueryBuilder.updateQueryStatementConditional(
          tagsQueryStatement, tagsWhereStatement);
      tags.forEach(tag -> params.add(tag.toUpperCase().trim()));
    }

    if (!favorited.isEmpty()) {
      String favoritedQueryStatement =
          "INNER JOIN ARTICLES_USERS articles_users ON articles.ID = articles_users.ARTICLE_ID "
              + "INNER JOIN USERS usersWhoFavorited ON articles_users.USER_ID = usersWhoFavorited.ID";
      String favoritedWhereStatement =
          String.format("UPPER(usersWhoFavorited.USERNAME) IN (%s)", listParams(favorited));
      findArticlesQueryBuilder.updateQueryStatementConditional(
          favoritedQueryStatement, favoritedWhereStatement);
      favorited.forEach(favorite -> params.add(favorite.toUpperCase().trim()));
    }

    if (!authors.isEmpty()) {
      String authorsWhereStatement =
          String.format("UPPER(users.USERNAME) IN (%s)", listParams(authors));
      findArticlesQueryBuilder.updateQueryStatementConditional(null, authorsWhereStatement);
      authors.forEach(author -> params.add(author.toUpperCase().trim()));
    }
  }

  @Override
  public Statement<JsonArray> totalArticles(
      List<String> tags, List<String> authors, List<String> favorited) {

    JsonArray params = new JsonArray();

    SimpleQueryBuilder totalArticlesQueryBuilder = new SimpleQueryBuilder();

    totalArticlesQueryBuilder.addQueryStatement(
        "SELECT COUNT(DISTINCT articles.ID) FROM ARTICLES articles "
            + "INNER JOIN USERS users ON articles.AUTHOR_ID = users.ID");

    configQueryBuilderFindArticles(tags, authors, favorited, params, totalArticlesQueryBuilder);

    return new JsonArrayStatement(totalArticlesQueryBuilder.toQueryString(), params);
  }

  @Override
  public Statement<JsonArray> findBySlug(String slug) {

    String sql = "SELECT * FROM ARTICLES WHERE SLUG = ?";

    JsonArray params = new JsonArray().add(slug);

    return new JsonArrayStatement(sql, params);
  }

  private String listParams(List<String> listParams) {
    return listParams.stream().map(param -> "?").collect(Collectors.joining(","));
  }
}
