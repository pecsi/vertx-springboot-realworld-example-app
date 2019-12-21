package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.user.model.User;
import com.example.realworld.domain.user.model.UserRepository;
import io.reactivex.Single;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryJDBC implements UserRepository {
  @Override
  public Single<User> store(User user) {
    return null;
  }

  @Override
  public Single<Integer> countByUsername(String username) {
    return null;
  }

  @Override
  public Single<Integer> countByEmail(String email) {
    return null;
  }
}
