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
}
