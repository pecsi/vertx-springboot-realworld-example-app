package com.example.realworld.application;

import com.example.realworld.domain.user.exception.EmailAlreadyExistsException;
import com.example.realworld.domain.user.exception.InvalidLoginException;
import com.example.realworld.domain.user.exception.UserNotFoundException;
import com.example.realworld.domain.user.exception.UsernameAlreadyExistsException;
import com.example.realworld.domain.user.model.*;
import com.example.realworld.domain.user.service.UserService;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;

import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl extends ApplicationService implements UserService {

  private UserRepository userRepository;
  private FollowedUsersRepository followedUsersRepository;
  private CryptographyProvider cryptographyProvider;
  private TokenProvider tokenProvider;
  private ModelValidator modelValidator;

  public UserServiceImpl(
      UserRepository userRepository,
      FollowedUsersRepository followedUsersRepository,
      CryptographyProvider cryptographyProvider,
      TokenProvider tokenProvider,
      ModelValidator modelValidator) {
    this.userRepository = userRepository;
    this.followedUsersRepository = followedUsersRepository;
    this.cryptographyProvider = cryptographyProvider;
    this.tokenProvider = tokenProvider;
    this.modelValidator = modelValidator;
  }

  @Override
  public Single<User> create(NewUser newUser) {
    modelValidator.validate(newUser);
    User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setUsername(newUser.getUsername());
    user.setEmail(newUser.getEmail());
    user.setPassword(cryptographyProvider.hashPassword(newUser.getPassword()));
    user.setToken(tokenProvider.generateToken(user.getId()));

    return validUsername(user.getUsername())
        .andThen(validEmail(user.getEmail()))
        .andThen(
            userRepository
                .store(user)
                .flatMap(
                    persistedUser ->
                        userRepository.findById(persistedUser.getId()).map(this::extractUser)));
  }

  private Completable validUsername(String username) {
    return isUsernameExists(username)
        .flatMapCompletable(
            isUsernameExists -> {
              if (isUsernameExists) {
                throw new UsernameAlreadyExistsException();
              }
              return CompletableObserver::onComplete;
            });
  }

  private Completable validEmail(String email) {
    return isEmailAlreadyExists(email)
        .flatMapCompletable(
            isEmailAlreadyExists -> {
              if (isEmailAlreadyExists) {
                throw new EmailAlreadyExistsException();
              }
              return CompletableObserver::onComplete;
            });
  }

  private User extractUser(Optional<User> userOptional) {
    return userOptional.orElseThrow(UserNotFoundException::new);
  }

  @Override
  public Single<User> login(Login login) {
    modelValidator.validate(login);
    return userRepository
        .findUserByEmail(login.getEmail())
        .flatMap(
            userOptional -> {
              if (!userOptional.isPresent()
                  || isPasswordInvalid(login.getPassword(), userOptional.get())) {
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
  public Single<User> update(UpdateUser updateUser, String exclusionId) {
    return checkValidations(updateUser, exclusionId)
        .andThen(
            userRepository
                .update(updateUser.toUser(exclusionId))
                .flatMap(user -> userRepository.findById(user.getId()).map(this::extractUser)));
  }

  @Override
  public Single<User> findByUsername(String username) {
    return userRepository
        .findUserByUsername(username)
        .map(userOptional -> userOptional.orElseThrow(UserNotFoundException::new));
  }

  @Override
  public Single<Boolean> isFollowing(String currentUserId, String followedUserId) {
    return followedUsersRepository
        .countByCurrentUserIdAndFollowedUserId(currentUserId, followedUserId)
        .map(this::isCountResultGreaterThanZero);
  }

  @Override
  public Completable follow(String currentUserId, String followedUserId) {
    return followedUsersRepository.follow(currentUserId, followedUserId);
  }

  @Override
  public Completable unfollow(String currentUserId, String followedUserId) {
    return followedUsersRepository.unfollow(currentUserId, followedUserId);
  }

  private Completable checkValidations(UpdateUser updateUser, String exclusionId) {
    return Single.just(isPresent(updateUser.getUsername()))
        .flatMap(
            usernameIsPresent -> {
              if (usernameIsPresent) {
                return isUsernameExists(updateUser.getUsername(), exclusionId);
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
                return isEmailAlreadyExists(updateUser.getEmail(), exclusionId);
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
    return !cryptographyProvider.isPasswordValid(password, user.getPassword());
  }

  private Single<Boolean> isUsernameExists(String username) {
    return userRepository.countByUsername(username).map(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isUsernameExists(String username, String exclusionId) {
    return userRepository
        .countByUsername(username, exclusionId)
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

  private boolean isPresent(String property) {
    return property != null && !property.isEmpty();
  }
}
