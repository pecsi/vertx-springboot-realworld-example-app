package com.example.realworld.application;

import com.example.realworld.domain.profile.model.Profile;
import com.example.realworld.domain.profile.service.ProfileService;
import com.example.realworld.domain.user.service.UserService;
import io.reactivex.Single;

public class ProfileServiceImpl extends ApplicationService implements ProfileService {

  private UserService userService;

  public ProfileServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Single<Profile> getProfile(String username, String loggedUserId) {
    return userService
        .findByUsername(username)
        .flatMap(
            user ->
                isFollowing(loggedUserId, user.getId())
                    .flatMap(isFollowing -> Single.just(new Profile(user, isFollowing))));
  }

  @Override
  public Single<Profile> follow(String username, String loggedUserId) {
    return null;
  }

  @Override
  public Single<Profile> unfollow(String username, String loggedUserId) {
    return null;
  }

  private Single<Boolean> isFollowing(String currentUserId, String followedUserId) {
    return Single.just(currentUserId != null)
        .flatMap(
            isLoggedUserIdPresent -> {
              if (isLoggedUserIdPresent) {
                return userService.isFollowing(currentUserId, followedUserId);
              } else {
                return Single.just(false);
              }
            });
  }
}
