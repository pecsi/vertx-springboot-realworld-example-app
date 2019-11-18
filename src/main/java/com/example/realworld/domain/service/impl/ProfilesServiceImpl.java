package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.entity.Profile;
import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.service.ProfilesService;
import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.statement.FollowedUsersStatements;
import com.example.realworld.domain.statement.Statement;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;

public class ProfilesServiceImpl extends AbstractService implements ProfilesService {

  private JDBCClient jdbcClient;
  private UsersService usersService;
  private FollowedUsersStatements followedUsersStatements;

  public ProfilesServiceImpl(
      JDBCClient jdbcClient,
      UsersService usersService,
      FollowedUsersStatements followedUsersStatements,
      ObjectMapper objectMapper) {
    super(objectMapper);
    this.jdbcClient = jdbcClient;
    this.usersService = usersService;
    this.followedUsersStatements = followedUsersStatements;
  }

  @Override
  public void getProfile(
      String username, Long loggedUserId, Handler<AsyncResult<Profile>> handler) {

    usersService.findByUsername(
        username,
        userAsyncResult -> {
          if (userAsyncResult.succeeded()) {
            User user = userAsyncResult.result();
            Single.just(loggedUserId != null)
                .flatMap(
                    isLoggedUserIdPresent -> {
                      if (isLoggedUserIdPresent) {
                        return isFollowing(loggedUserId, user.getId());
                      } else {
                        return Single.just(false);
                      }
                    })
                .subscribe(
                    isFollowing ->
                        handler.handle(Future.succeededFuture(new Profile(user, isFollowing))),
                    throwable -> handler.handle(error(throwable)));
          } else {
            handler.handle(Future.failedFuture(userAsyncResult.cause()));
          }
        });
  }

  @Override
  public void follow(Long loggedUserId, String username, Handler<AsyncResult<Profile>> handler) {

    usersService.findByUsername(
        username,
        userAsyncResult -> {
          if (userAsyncResult.succeeded()) {
            User user = userAsyncResult.result();
            follow(loggedUserId, user.getId())
                .subscribe(
                    () -> getProfile(username, loggedUserId, handler),
                    throwable -> handler.handle(error(throwable)));
          } else {
            handler.handle(Future.failedFuture(userAsyncResult.cause()));
          }
        });
  }

  @Override
  public void unfollow(Long loggedUserId, String username, Handler<AsyncResult<Profile>> handler) {}

  private Single<Boolean> isFollowing(Long currentUserId, Long followedUserId) {
    Statement<JsonArray> isFollowingStatement =
        followedUsersStatements.isFollowing(currentUserId, followedUserId);
    return SQLClientHelper.inTransactionSingle(
        jdbcClient,
        sqlConnection ->
            sqlConnection
                .rxQueryWithParams(isFollowingStatement.sql(), isFollowingStatement.params())
                .map(this::isCountResultGreaterThanZero));
  }

  private Completable follow(Long currentUserId, Long followedId) {
    Statement<JsonArray> followStatement =
        followedUsersStatements.follow(currentUserId, followedId);
    return SQLClientHelper.inTransactionCompletable(
        jdbcClient,
        sqlConnection ->
            sqlConnection
                .rxQueryWithParams(followStatement.sql(), followStatement.params())
                .flatMapCompletable(resultSet -> Completable.complete()));
  }
}
