package com.example.realworld.application;

import com.example.realworld.domain.user.CryptographyService;
import com.example.realworld.domain.user.exception.EmailAlreadyExistsException;
import com.example.realworld.domain.user.exception.UsernameAlreadyExistsException;
import com.example.realworld.domain.user.model.NewUser;
import com.example.realworld.domain.user.model.TokenProvider;
import com.example.realworld.domain.user.model.User;
import com.example.realworld.domain.user.model.UserRepository;
import com.example.realworld.domain.user.service.UserService;
import io.reactivex.Single;

import java.util.UUID;

public class UserServiceImpl implements UserService {

  private UserRepository userRepository;
  private CryptographyService cryptographyService;
  private TokenProvider tokenProvider;

  public UserServiceImpl(
      UserRepository userRepository,
      CryptographyService cryptographyService,
      TokenProvider tokenProvider) {
    this.userRepository = userRepository;
    this.cryptographyService = cryptographyService;
    this.tokenProvider = tokenProvider;
  }

  @Override
  public Single<User> create(NewUser newUser) {

    User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setUsername(newUser.getUsername());
    user.setEmail(newUser.getEmail());
    user.setPassword(cryptographyService.hashPassword(newUser.getPassword()));
    user.setToken(tokenProvider.generateToken(user.getId()));

    return isUsernameExists(user.getUsername())
        .flatMap(
            isUsernameExists -> {
              if (isUsernameExists) {
                throw new UsernameAlreadyExistsException();
              }
              return isEmailAlreadyExists(user.getEmail())
                  .flatMap(
                      isEmailAlreadyExists -> {
                        if (isEmailAlreadyExists) {
                          throw new EmailAlreadyExistsException();
                        }
                        return userRepository
                            .store(user)
                            .flatMap(
                                persistedUser -> userRepository.findById(persistedUser.getId()));
                      });
            });
  }

  private Single<Boolean> isUsernameExists(String username) {
    return userRepository.countByUsername(username).flatMap(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isEmailAlreadyExists(String email) {
    return userRepository.countByEmail(email).flatMap(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isCountResultGreaterThanZero(Long countResult) {
    if (countResult > 0) {
      return Single.just(true);
    } else {
      return Single.just(false);
    }
  }
}
