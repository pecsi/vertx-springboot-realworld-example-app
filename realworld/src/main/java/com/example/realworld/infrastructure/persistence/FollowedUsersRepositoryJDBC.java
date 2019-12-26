package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.user.model.FollowedUsersRepository;
import com.example.realworld.infrastructure.persistence.statement.FollowedUsersStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

@Repository
public class FollowedUsersRepositoryJDBC extends JDBCRepository implements FollowedUsersRepository {

  private JDBCClient jdbcClient;
  private FollowedUsersStatements followedUsersStatements;

  public FollowedUsersRepositoryJDBC(
      JDBCClient jdbcClient, FollowedUsersStatements followedUsersStatements) {
    this.jdbcClient = jdbcClient;
    this.followedUsersStatements = followedUsersStatements;
  }

  @Override
  public Single<Long> countByCurrentUserIdAndFollowedUserId(
      String currentUserId, String followedUserId) {
    Statement<JsonArray> countByCurrentUserIdAndFollowedUserIdStatement =
        followedUsersStatements.countByCurrentUserIdAndFollowedUserId(
            currentUserId, followedUserId);
    return jdbcClient
        .rxQueryWithParams(
            countByCurrentUserIdAndFollowedUserIdStatement.sql(),
            countByCurrentUserIdAndFollowedUserIdStatement.params())
        .map(this::getCountFromResultSet);
  }
}
