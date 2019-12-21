package com.example.realworld.infrastructure.persistence.statement;

import io.vertx.core.json.JsonArray;

public interface FollowedUsersStatements {

  Statement<JsonArray> isFollowing(Long currentUserId, Long followedUserId);

  Statement<JsonArray> follow(Long currentUserId, Long followedUserId);
}
