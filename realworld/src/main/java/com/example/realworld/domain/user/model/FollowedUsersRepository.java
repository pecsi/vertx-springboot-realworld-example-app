package com.example.realworld.domain.user.model;

import io.reactivex.Single;

public interface FollowedUsersRepository {
  Single<Long> countByCurrentUserIdAndFollowedUserId(String currentUserId, String followedUserId);
}
