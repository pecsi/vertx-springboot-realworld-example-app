package com.example.realworld.domain.user.service;

import com.example.realworld.domain.user.model.NewUser;
import com.example.realworld.domain.user.model.User;
import io.reactivex.Single;

public interface UserService {

  Single<User> create(NewUser newUser);

  //  Single<User> login(String email, String password);
  //
  //  Single<User> findById(Long userId);
  //
  //  Single<User> update(User user);
  //
  //  Single<User> findByUsername(String username);
}
