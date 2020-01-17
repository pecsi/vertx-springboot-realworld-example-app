package com.example.realworld.domain.profile.model;

import com.example.realworld.domain.article.model.Article;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

public interface UsersFollowedRepository {
  Single<Long> countByCurrentUserIdAndFollowedUserId(String currentUserId, String userFollowedId);

  Completable follow(String currentUserId, String userFollowedId);

  Completable unfollow(String currentUserId, String userFollowedId);

  Single<List<Article>> findRecentArticles(String currentUserId, int offset, int limit);

  Single<Long> totalUserArticlesFollowed(String currentUserId);
}
