package com.example.realworld.domain.profile.service;

import com.example.realworld.domain.profile.model.Profile;
import io.reactivex.Single;

public interface ProfileService {

  Single<Profile> getProfile(String username, String loggedUserId);

  Single<Profile> follow(String username, String loggedUserId);

  Single<Profile> unfollow(String username, String loggedUserId);
}
