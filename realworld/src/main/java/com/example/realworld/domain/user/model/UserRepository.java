package com.example.realworld.domain.user.model;

import io.reactivex.Single;

public interface UserRepository {
  Single<User> store(User user);

  Single<Long> countByUsername(String username);

  Single<Long> countByEmail(String email);

  Single<User> findById(String id);
}
