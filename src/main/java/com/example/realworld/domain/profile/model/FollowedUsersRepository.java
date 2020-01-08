package com.example.realworld.domain.profile.model;

import com.example.realworld.domain.article.model.Article;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

public interface FollowedUsersRepository {
  Single<Long> countByCurrentUserIdAndFollowedUserId(String currentUserId, String followedUserId);

  Completable follow(String currentUserId, String followedUserId);

  Completable unfollow(String currentUserId, String followedUserId);

  Single<List<Article>> findRecentArticles(String currentUserId, int offset, int limit);

  Single<Long> totalUserArticlesFollowed(String currentUserId);
}
