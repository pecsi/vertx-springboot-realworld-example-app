package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.FavoritesRepository;
import com.example.realworld.infrastructure.persistence.statement.ArticlesUsersStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

@Repository
public class FavoritesRepositoryJDBC extends JDBCRepository implements FavoritesRepository {

  private JDBCClient jdbcClient;
  private ArticlesUsersStatements articlesUsersStatements;

  public FavoritesRepositoryJDBC(
      JDBCClient jdbcClient, ArticlesUsersStatements articlesUsersStatements) {
    this.jdbcClient = jdbcClient;
    this.articlesUsersStatements = articlesUsersStatements;
  }

  @Override
  public Single<Long> countByArticleIdAndUserId(String articleId, String userId) {
    Statement<JsonArray> countByArticleIdAndUserIdStatement =
        articlesUsersStatements.countByArticleIdAndUserId(articleId, userId);
    return jdbcClient
        .rxQueryWithParams(
            countByArticleIdAndUserIdStatement.sql(), countByArticleIdAndUserIdStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Long> countByArticleId(String articleId) {
    Statement<JsonArray> countByArticleId = articlesUsersStatements.countByArticleId(articleId);
    return jdbcClient
        .rxQueryWithParams(countByArticleId.sql(), countByArticleId.params())
        .map(this::getCountFromResultSet);
  }
}
