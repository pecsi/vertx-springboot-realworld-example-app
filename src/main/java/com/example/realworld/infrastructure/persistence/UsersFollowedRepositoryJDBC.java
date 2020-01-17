package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.profile.model.UsersFollowedRepository;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.statement.UsersFollowedStatements;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsersFollowedRepositoryJDBC extends JDBCRepository implements UsersFollowedRepository {

  private JDBCClient jdbcClient;
  private UsersFollowedStatements usersFollowedStatements;

  public UsersFollowedRepositoryJDBC(
      JDBCClient jdbcClient, UsersFollowedStatements usersFollowedStatements) {
    this.jdbcClient = jdbcClient;
    this.usersFollowedStatements = usersFollowedStatements;
  }

  @Override
  public Single<Long> countByCurrentUserIdAndFollowedUserId(
      String currentUserId, String userFollowedId) {
    Statement<JsonArray> countByCurrentUserIdAndUserFollowedIdStatement =
        usersFollowedStatements.countByCurrentUserIdAndUserFollowedId(
            currentUserId, userFollowedId);
    return jdbcClient
        .rxQueryWithParams(
            countByCurrentUserIdAndUserFollowedIdStatement.sql(),
            countByCurrentUserIdAndUserFollowedIdStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Completable follow(String currentUserId, String userFollowedId) {
    Statement<JsonArray> followStatement =
        usersFollowedStatements.follow(currentUserId, userFollowedId);
    return jdbcClient
        .rxUpdateWithParams(followStatement.sql(), followStatement.params())
        .flatMapCompletable(updateResult -> Completable.complete());
  }

  @Override
  public Completable unfollow(String currentUserId, String userFollowedId) {
    Statement<JsonArray> unfollowStatement =
        usersFollowedStatements.unfollow(currentUserId, userFollowedId);
    return jdbcClient
        .rxUpdateWithParams(unfollowStatement.sql(), unfollowStatement.params())
        .flatMapCompletable(updateResult -> Completable.complete());
  }

  @Override
  public Single<List<Article>> findRecentArticles(String currentUserId, int offset, int limit) {
    Statement<JsonArray> findRecentArticlesStatement =
        usersFollowedStatements.findRecentArticles(currentUserId, offset, limit);
    return jdbcClient
        .rxQueryWithParams(findRecentArticlesStatement.sql(), findRecentArticlesStatement.params())
        .map(ParserUtils::toArticleList);
  }

  @Override
  public Single<Long> totalUserArticlesFollowed(String currentUserId) {
    Statement<JsonArray> totalUserArticlesFollowedStatement =
        usersFollowedStatements.totalUserArticlesFollowed(currentUserId);
    return jdbcClient
        .rxQueryWithParams(
            totalUserArticlesFollowedStatement.sql(), totalUserArticlesFollowedStatement.params())
        .map(resultSet -> resultSet.getRows().get(0).getLong("COUNT(DISTINCT ARTICLES.ID)"));
  }
}
