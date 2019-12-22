package com.example.realworld.application;

import com.example.realworld.domain.user.CryptographyService;
import com.example.realworld.domain.user.exception.EmailAlreadyExistsException;
import com.example.realworld.domain.user.exception.InvalidLoginException;
import com.example.realworld.domain.user.exception.UserNotFoundException;
import com.example.realworld.domain.user.exception.UsernameAlreadyExistsException;
import com.example.realworld.domain.user.model.*;
import com.example.realworld.domain.user.service.UserService;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl extends ApplicationService implements UserService {

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
                                persistedUser ->
                                    userRepository
                                        .findById(persistedUser.getId())
                                        .map(this::extractUser));
                      });
            });
  }

  private User extractUser(Optional<User> userOptional) {
    return userOptional.orElseThrow(UserNotFoundException::new);
  }

  @Override
  public Single<User> login(String email, String password) {
    return userRepository
        .findUserByEmail(email)
        .flatMap(
            userOptional -> {
              if (!userOptional.isPresent() || isPasswordInvalid(password, userOptional.get())) {
                throw new InvalidLoginException();
              }
              User user = userOptional.get();
              user.setToken(tokenProvider.generateToken(user.getId()));
              return userRepository.update(user);
            });
  }

  @Override
  public Single<User> findById(String userId) {
    return userRepository
        .findById(userId)
        .map(userOptional -> userOptional.orElseThrow(UserNotFoundException::new));
  }

  @Override
  public Single<User> update(UpdateUser updateUser, String excludeUserId) {
    return checkValidations(updateUser, excludeUserId)
        .andThen(
            userRepository
                .update(updateUser.toUser(excludeUserId))
                .flatMap(user -> userRepository.findById(user.getId()).map(this::extractUser)));
  }

  private Completable checkValidations(UpdateUser updateUser, String excludeUserId) {
    return Single.just(isPresent(updateUser.getUsername()))
        .flatMap(
            usernameIsPresent -> {
              if (usernameIsPresent) {
                return isUsernameExists(updateUser.getUsername(), excludeUserId);
              }
              return Single.just(false);
            })
        .flatMap(
            isUsernameExists -> {
              if (isUsernameExists) {
                throw new UsernameAlreadyExistsException();
              }
              return Single.just(isPresent(updateUser.getEmail()));
            })
        .flatMap(
            emailIsPresent -> {
              if (emailIsPresent) {
                return isEmailAlreadyExists(updateUser.getEmail(), excludeUserId);
              }
              return Single.just(false);
            })
        .flatMapCompletable(
            isEmailAlreadyExists -> {
              if (isEmailAlreadyExists) {
                throw new EmailAlreadyExistsException();
              }
              return Completable.complete();
            });
  }

  private boolean isPasswordInvalid(String password, User user) {
    return !cryptographyService.isPasswordValid(password, user.getPassword());
  }

  private Single<Boolean> isUsernameExists(String username) {
    return userRepository.countByUsername(username).map(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isUsernameExists(String username, String excludeUserId) {
    return userRepository
        .countByUsername(username, excludeUserId)
        .map(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isEmailAlreadyExists(String email) {
    return userRepository.countByEmail(email).map(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isEmailAlreadyExists(String email, String excludeUserId) {
    return userRepository
        .countByEmail(email, excludeUserId)
        .map(this::isCountResultGreaterThanZero);
  }

  private boolean isCountResultGreaterThanZero(Long countResult) {
    return countResult > 0;
  }

  private boolean isPresent(String property) {
    return property != null && !property.isEmpty();
  }
}
