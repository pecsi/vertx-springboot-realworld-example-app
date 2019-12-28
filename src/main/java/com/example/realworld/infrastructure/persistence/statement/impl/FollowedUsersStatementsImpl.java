package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.infrastructure.persistence.statement.FollowedUsersStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

@Component
public class FollowedUsersStatementsImpl implements FollowedUsersStatements {

  @Override
  public Statement<JsonArray> countByCurrentUserIdAndFollowedUserId(
      String currentUserId, String followedUserId) {

    String sql = "SELECT COUNT(*) FROM FOLLOWED_USERS WHERE USER_ID = ? AND FOLLOWED_ID = ?";

    JsonArray params = new JsonArray().add(currentUserId).add(followedUserId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> follow(String currentUserId, String followedUserId) {

    String sql = "INSERT INTO FOLLOWED_USERS (USER_ID, FOLLOWED_ID) VALUES (?, ?)";

    JsonArray params = new JsonArray().add(currentUserId).add(followedUserId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> unfollow(String currentUserId, String followedUserId) {

    String sql = "DELETE FROM FOLLOWED_USERS WHERE USER_ID = ? AND FOLLOWED_ID = ?";

    JsonArray params = new JsonArray().add(currentUserId).add(followedUserId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> findRecentArticles(String currentUserId, int offset, int limit) {

    String sql =
        "SELECT articles.id, articles.title FROM FOLLOWED_USERS followed_users INNER JOIN USERS users1 ON followed_users.USER_ID = users1.ID AND (users1.ID = ?) INNER JOIN USERS users2 ON followed_users.FOLLOWED_ID = users2.ID INNER JOIN ARTICLES articles ON users2.ID = articles.AUTHOR_ID order by articles.CREATED_AT desc limit ? offset ?";

    JsonArray params = new JsonArray().add(currentUserId).add(offset).add(limit);

    return new JsonArrayStatement(sql, params);
  }
}
