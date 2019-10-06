package com.example.realworld.domain.repository.impl;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.repository.UserRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

import java.util.LinkedList;
import java.util.List;

public class UserRepositoryImpl extends AbstractRepository implements UserRepository {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public UserRepositoryImpl(SQLClient sqlClient) {
    super(sqlClient);
  }

  @Override
  public void create(User user, Handler<AsyncResult<User>> handler) {

    String sql =
        "INSERT INTO USERS (USERNAME, BIO, EMAIL, IMAGE, PASSWORD, TOKEN) VALUES (?, ?, ?, ?, ?, ?)";

    JsonArray params =
        new JsonArray()
            .add(user.getUsername())
            .add(user.getBio())
            .add(user.getEmail())
            .add(user.getImage())
            .add(user.getPassword())
            .add(user.getToken());

    create(
        sql,
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

  @Override
  public void update(User user, Handler<AsyncResult<User>> handler) {

    List<String> fields = new LinkedList<>();
    JsonArray params = new JsonArray();

    if (isPresent(user.getUsername())) {
      fields.add("USERNAME = ?");
      params.add(user.getUsername());
    }

    if (isPresent(user.getBio())) {
      fields.add("BIO = ?");
      params.add(user.getBio());
    }

    if (isPresent(user.getEmail())) {
      fields.add("EMAIL = ?");
      params.add(user.getEmail());
    }

    if (isPresent(user.getImage())) {
      fields.add("IMAGE = ?");
      params.add(user.getImage());
    }

    if (isPresent(user.getPassword())) {
      fields.add("PASSWORD = ?");
      params.add(user.getPassword());
    }

    if (isPresent(user.getToken())) {
      fields.add("TOKEN = ?");
      params.add(user.getToken());
    }

    params.add(user.getId());

    String sql = "UPDATE USERS SET " + String.join(", ", fields) + " WHERE ID = ?";

    update(
        sql,
        params,
        updateAsyncResult -> {
          if (updateAsyncResult.succeeded()) {
            handler.handle(Future.succeededFuture(user));
          } else {
            handler.handle(Future.failedFuture(logCause(updateAsyncResult.cause())));
          }
        });
  }

  @Override
  public void find(Long id, Handler<AsyncResult<User>> handler) {

    String sql = "SELECT * FROM USERS WHERE ID = ?";

    find(
        sql,
        new JsonArray().add(id),
        findAsyncResult -> {
          if (findAsyncResult.succeeded()) {
            ResultSet result = findAsyncResult.result();

            JsonObject row = result.getRows().get(0);

            User user = new User();
            user.setId(row.getLong("ID"));
            user.setUsername(row.getString("USERNAME"));
            user.setBio(row.getString("BIO"));
            user.setImage(row.getString("IMAGE"));
            user.setPassword(row.getString("PASSWORD"));
            user.setEmail(row.getString("EMAIL"));
            user.setToken(row.getString("TOKEN"));

            handler.handle(Future.succeededFuture(user));
          } else {
            handler.handle(Future.failedFuture(findAsyncResult.cause()));
          }
        });
  }

  private boolean isPresent(String value) {
    return value != null && !value.isEmpty();
  }

  private Throwable logCause(Throwable throwable) {
    logger.error(throwable.getMessage());
    return throwable;
  }
}
