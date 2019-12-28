package com.example.realworld.domain.user.model;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface FollowedUsersRepository {
  Single<Long> countByCurrentUserIdAndFollowedUserId(String currentUserId, String followedUserId);

  Completable follow(String currentUserId, String followedUserId);

  Completable unfollow(String currentUserId, String followedUserId);
}
