package com.example.realworld.domain.user.service;

import com.example.realworld.domain.user.model.NewUser;
import com.example.realworld.domain.user.model.UpdateUser;
import com.example.realworld.domain.user.model.User;
import io.reactivex.Single;

public interface UserService {

  Single<User> create(NewUser newUser);

  Single<User> login(String email, String password);

  Single<User> findById(String userId);

  Single<User> update(UpdateUser updateUser, String excludeUserId);
  //
  //  Single<User> findByUsername(String username);
}
