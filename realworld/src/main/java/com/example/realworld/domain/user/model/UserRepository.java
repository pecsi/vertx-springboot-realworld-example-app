package com.example.realworld.domain.user.model;

import io.reactivex.Single;

public interface UserRepository {
  Single<User> store(User user);

  Single<Integer> countByUsername(String username);

  Single<Integer> countByEmail(String email);
}
