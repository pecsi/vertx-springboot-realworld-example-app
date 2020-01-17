package com.example.realworld.infrastructure.persistence.statement;

import io.vertx.core.json.JsonArray;

public interface UsersFollowedStatements {

  Statement<JsonArray> countByCurrentUserIdAndUserFollowedId(
      String currentUserId, String userFollowedId);

  Statement<JsonArray> follow(String currentUserId, String userFollowedId);

  Statement<JsonArray> unfollow(String currentUserId, String userFollowedId);

  Statement<JsonArray> findRecentArticles(String currentUserId, int offset, int limit);

  Statement<JsonArray> totalUserArticlesFollowed(String currentUserId);
}
