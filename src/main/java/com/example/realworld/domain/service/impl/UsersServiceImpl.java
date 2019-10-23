package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.exception.UsernameAlreadyExistsException;
import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.service.error.Error;
import com.example.realworld.domain.statement.Statement;
import com.example.realworld.domain.statement.UserStatements;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.serviceproxy.ServiceException;

public class UsersServiceImpl implements UsersService {

  private UserStatements userStatements;
  private JWTAuth jwtProvider;
  private JDBCClient jdbcClient;
  private ObjectMapper objectMapper;

  public UsersServiceImpl(
      UserStatements userStatements,
      JWTAuth jwtProvider,
      JDBCClient jdbcClient,
      ObjectMapper objectMapper) {
    this.userStatements = userStatements;
    this.jwtProvider = jwtProvider;
    this.jdbcClient = jdbcClient;
    this.objectMapper = objectMapper;
  }

  @Override
  public void create(
      String username, String email, String password, Handler<AsyncResult<User>> handler) {

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);

    SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection -> {
              Statement<JsonArray> existByUsernameStatement =
                  userStatements.existBy("username", username);

              Statement<JsonArray> createUserStatement = userStatements.create(user);

              return sqlConnection
                  .rxQueryWithParams(
                      existByUsernameStatement.sql(), existByUsernameStatement.params())
                  .map(resultSet -> resultSet.getRows().get(0).getLong("COUNT(*)") > 0)
                  .flatMap(
                      isUsernameExists -> {
                        if (isUsernameExists) {
                          throw new UsernameAlreadyExistsException();
                        }
                        return sqlConnection
                            .rxUpdateWithParams(
                                createUserStatement.sql(), createUserStatement.params())
                            .map(
                                updateResult -> {
                                  Long id = updateResult.getKeys().getLong(0);
                                  user.setId(id);
                                  return user;
                                })
                            .flatMap(
                                createdUser -> {
                                  createdUser.setToken(
                                      jwtProvider.generateToken(
                                          new JsonObject().put("sub", createdUser.getId())));

                                  Statement<JsonArray> updateUserStatement =
                                      userStatements.update(createdUser);
                                  return sqlConnection.rxUpdateWithParams(
                                      updateUserStatement.sql(), updateUserStatement.params());
                                })
                            .flatMap(
                                updateResult -> {
                                  Statement<JsonArray> findByIdStatement =
                                      userStatements.findById(user.getId());
                                  return sqlConnection.rxQueryWithParams(
                                      findByIdStatement.sql(), findByIdStatement.params());
                                });
                      });
            })
        .map(this::toUser)
        .subscribe(
            resultUser -> handler.handle(Future.succeededFuture(resultUser)),
            throwable -> handler.handle(error(throwable)));
  }

  private <T> AsyncResult<T> error(Throwable throwable) {
    String error;
    try {
      error =
          objectMapper.writeValueAsString(new Error<>(throwable.getClass().getName(), throwable));
    } catch (JsonProcessingException ex) {
      error = ex.getMessage();
    }
    return ServiceException.fail(1, error);
  }

  private User toUser(ResultSet resultSet) {
    JsonObject row = resultSet.getRows().get(0);
    User user = new User();
    user.setId(row.getLong("ID"));
    user.setUsername(row.getString("USERNAME"));
    user.setBio(row.getString("BIO"));
    user.setImage(row.getString("IMAGE"));
    user.setPassword(row.getString("PASSWORD"));
    user.setEmail(row.getString("EMAIL"));
    user.setToken(row.getString("TOKEN"));
    return user;
  }
}
