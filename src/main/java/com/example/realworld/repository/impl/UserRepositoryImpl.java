package com.example.realworld.repository.impl;

import com.example.realworld.domain.entity.User;
import com.example.realworld.repository.UserRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLClient;

public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public UserRepositoryImpl(SQLClient sqlClient) {
    super(sqlClient);
  }

  @Override
  public void create(User user, Handler<AsyncResult<User>> handler) {

    String query =
        "INSERT INTO USERS (USERNAME, BIO, EMAIL, IMAGE, PASSWORD, TOKEN) VALUES (?, ?, ?, ?, ?, ?)";

    JsonArray params =
        new JsonArray()
            .add(user.getUsername())
            .add(user.getBio())
            .add(user.getEmail())
            .add(user.getImage())
            .add(user.getPassword())
            .add(user.getToken());

    updateWithParams(
        query,
        params,
        createAsyncResult -> {
          if (createAsyncResult.succeeded()) {
            user.setId(createAsyncResult.result());
            handler.handle(Future.succeededFuture(user));
          } else {
            handler.handle(Future.failedFuture(logCause(createAsyncResult.cause())));
          }
        });
  }

  private Throwable logCause(Throwable throwable) {
    logger.error(throwable.getMessage());
    return throwable;
  }
}
