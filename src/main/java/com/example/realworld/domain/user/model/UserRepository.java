package com.example.realworld.domain.user.model;

import io.reactivex.Single;

import java.util.Optional;

public interface UserRepository {
  Single<User> store(User user);

  Single<Long> countByUsername(String username);

  Single<Long> countByUsername(String username, String exclusionId);

  Single<Long> countByEmail(String email);

  Single<Long> countByEmail(String email, String excludeUserId);

  Single<Optional<User>> findById(String id);

  Single<Optional<User>> findUserByEmail(String email);

  Single<User> update(User user);

  Single<Optional<User>> findUserByUsername(String username);
}
