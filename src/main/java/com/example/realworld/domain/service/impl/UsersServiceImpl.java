package com.example.realworld.domain.service.impl;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.exception.EmailAlreadyExistsException;
import com.example.realworld.domain.exception.UsernameAlreadyExistsException;
import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.statement.Statement;
import com.example.realworld.domain.statement.UserStatements;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.ext.sql.SQLConnection;
import org.mindrot.jbcrypt.BCrypt;

public class UsersServiceImpl extends AbstractService implements UsersService {

  private UserStatements userStatements;
  private JWTAuth jwtProvider;
  private JDBCClient jdbcClient;

  public UsersServiceImpl(
      UserStatements userStatements,
      JWTAuth jwtProvider,
      JDBCClient jdbcClient,
      ObjectMapper objectMapper) {
    super(objectMapper);
    this.userStatements = userStatements;
    this.jwtProvider = jwtProvider;
    this.jdbcClient = jdbcClient;
  }

  @Override
  public void create(
      String username, String email, String password, Handler<AsyncResult<User>> handler) {

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

    SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                isUsernameExists(sqlConnection, username)
                    .flatMap(
                        isUsernameExists -> {
                          if (isUsernameExists) {
                            throw new UsernameAlreadyExistsException();
                          }
                          return isEmailAlreadyExists(sqlConnection, email)
                              .flatMap(
                                  isEmailAlreadyExists -> {
                                    if (isEmailAlreadyExists) {
                                      throw new EmailAlreadyExistsException();
                                    }
                                    return createUser(sqlConnection, user)
                                        .flatMap(
                                            userCreated ->
                                                setUserToken(sqlConnection, userCreated)
                                                    .flatMap(
                                                        updateResult ->
                                                            findUserById(
                                                                sqlConnection,
                                                                userCreated.getId())))
                                        .map(this::toUser);
                                  });
                        }))
        .subscribe(
            resultUser -> handler.handle(Future.succeededFuture(resultUser)),
            throwable -> handler.handle(error(throwable)));
  }

  private Single<Boolean> isUsernameExists(SQLConnection sqlConnection, String username) {
    Statement<JsonArray> existByUsernameStatement = userStatements.existBy("username", username);
    return sqlConnection
        .rxQueryWithParams(existByUsernameStatement.sql(), existByUsernameStatement.params())
        .map(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isEmailAlreadyExists(SQLConnection sqlConnection, String email) {
    Statement<JsonArray> existByEmailStatement = userStatements.existBy("email", email);
    return sqlConnection
        .rxQueryWithParams(existByEmailStatement.sql(), existByEmailStatement.params())
        .map(this::isCountResultGreaterThanZero);
  }

  private Single<User> createUser(SQLConnection sqlConnection, User user) {
    Statement<JsonArray> createUserStatement = userStatements.create(user);
    return sqlConnection
        .rxUpdateWithParams(createUserStatement.sql(), createUserStatement.params())
        .map(
            updateResult -> {
              Long id = updateResult.getKeys().getLong(0);
              user.setId(id);
              return user;
            });
  }

  private Single<UpdateResult> setUserToken(SQLConnection sqlConnection, User user) {
    user.setToken(jwtProvider.generateToken(new JsonObject().put("sub", user.getId())));
    Statement<JsonArray> updateUserStatement = userStatements.update(user);
    return sqlConnection.rxUpdateWithParams(
        updateUserStatement.sql(), updateUserStatement.params());
  }

  private Single<ResultSet> findUserById(SQLConnection sqlConnection, Long id) {
    Statement<JsonArray> findByIdStatement = userStatements.findById(id);
    return sqlConnection.rxQueryWithParams(findByIdStatement.sql(), findByIdStatement.params());
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

  private boolean isCountResultGreaterThanZero(ResultSet resultSet) {
    return resultSet.getRows().get(0).getLong("COUNT(*)") > 0;
  }
}
