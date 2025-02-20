package com.example.realworld.application;

import com.example.realworld.application.data.ProfileData;
import com.example.realworld.domain.profile.exception.SelfFollowException;
import com.example.realworld.domain.profile.service.ProfileService;
import com.example.realworld.domain.user.exception.UserAlreadyFollowedException;
import com.example.realworld.domain.user.service.UserService;
import io.reactivex.Completable;
import io.reactivex.Single;

public class ProfileServiceImpl extends ApplicationService implements ProfileService {

  private UserService userService;

  public ProfileServiceImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Single<ProfileData> getProfile(String username, String loggedUserId) {
    return userService
        .findByUsername(username)
        .flatMap(
            user ->
                isFollowing(loggedUserId, user.getId())
                    .flatMap(isFollowing -> Single.just(new ProfileData(user, isFollowing))));
  }

  @Override
  public Single<ProfileData> follow(String username, String loggedUserId) {
    return userService
        .findByUsername(username)
        .flatMap(
            user ->
                validSelfFollow(loggedUserId, user.getId())
                    .andThen(validAlreadyFollowing(loggedUserId, user.getId()))
                    .andThen(userService.follow(loggedUserId, user.getId()))
                    .andThen(getProfile(username, loggedUserId)));
  }

  private Completable validAlreadyFollowing(String currentUserId, String userFollowedId) {
    return isFollowing(currentUserId, userFollowedId)
        .flatMapCompletable(
            isFollowing -> {
              if (isFollowing) {
                throw new UserAlreadyFollowedException();
              }
              return Completable.complete();
            });
  }

  private Completable validSelfFollow(String loggedUserId, String userFollowedId) {
    return Completable.create(
        completableEmitter -> {
          if (loggedUserId.equals(userFollowedId)) {
            throw new SelfFollowException();
          }
          completableEmitter.onComplete();
        });
  }

  @Override
  public Single<ProfileData> unfollow(String username, String loggedUserId) {
    return userService
        .findByUsername(username)
        .flatMap(
            user ->
                userService
                    .unfollow(loggedUserId, user.getId())
                    .andThen(getProfile(username, loggedUserId)));
  }

  @Override
  public Single<ProfileData> getProfileById(String currentUserId) {
    return userService.findById(currentUserId).map(ProfileData::new);
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
