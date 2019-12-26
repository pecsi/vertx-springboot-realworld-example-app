package com.example.realworld.infrastructure.persistence.statement;

import io.vertx.core.json.JsonArray;

public interface FollowedUsersStatements {

  Statement<JsonArray> countByCurrentUserIdAndFollowedUserId(
      String currentUserId, String followedUserId);

  Statement<JsonArray> follow(Long currentUserId, Long followedUserId);
}
