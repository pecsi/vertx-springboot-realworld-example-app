package com.example.realworld.domain.statement.impl;

import com.example.realworld.domain.statement.FollowedUsersStatements;
import com.example.realworld.domain.statement.Statement;
import io.vertx.core.json.JsonArray;

public class FollowedUsersStatementsImpl implements FollowedUsersStatements {
  @Override
  public Statement<JsonArray> isFollowing(Long currentUserId, Long followedUserId) {

    String sql = "SELECT COUNT(*) FROM FOLLOWED_USERS WHERE USER_ID = ? AND FOLLOWED_ID = ?";

    JsonArray params = new JsonArray().add(currentUserId).add(followedUserId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> follow(Long currentUserId, Long followedUserId) {

    String sql = "INSERT INTO FOLLOWED_USERS (USER_ID, FOLLOWED_ID) VALUES (?, ?)";

    JsonArray params = new JsonArray().add(currentUserId).add(followedUserId);

    return new JsonArrayStatement(sql, params);
  }
}
