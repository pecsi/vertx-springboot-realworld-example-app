package com.example.realworld.domain.user.service;

import com.example.realworld.domain.user.model.Login;
import com.example.realworld.domain.user.model.NewUser;
import com.example.realworld.domain.user.model.UpdateUser;
import com.example.realworld.domain.user.model.User;
import io.reactivex.Single;

public interface UserService {

  Single<User> create(NewUser newUser);

  Single<User> login(Login login);

  Single<User> findById(String userId);

  Single<User> update(UpdateUser updateUser, String excludeUserId);

  Single<User> findByUsername(String username);

  Single<Boolean> isFollowing(String currentUserId, String followedUserId);
}
