package com.example.realworld.domain.profile.service;

import com.example.realworld.application.data.ProfileData;
import io.reactivex.Single;

public interface ProfileService {

  Single<ProfileData> getProfile(String username, String loggedUserId);

  Single<ProfileData> follow(String username, String loggedUserId);

  Single<ProfileData> unfollow(String username, String loggedUserId);

  Single<ProfileData> getProfileById(String currentUserId);
}
