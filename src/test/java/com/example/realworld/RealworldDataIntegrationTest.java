package com.example.realworld;

import com.example.realworld.domain.user.model.User;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import org.junit.jupiter.api.AfterEach;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.UUID;

public class RealworldDataIntegrationTest extends RealworldApplicationIntegrationTest {

  @AfterEach
  public void afterEach(VertxTestContext vertxTestContext) {
    clearDatabase(vertxTestContext);
  }

  protected Single<User> createUser(User user) {
    user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
    user.setToken(
        jwtAuth.generateToken(
            new JsonObject()
                .put("sub", user.getId())
                .put("complementary-subscription", UUID.randomUUID().toString())));
    Statement<JsonArray> createUserStatement = userStatements.create(user);
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxUpdateWithParams(
                    createUserStatement.sql(), createUserStatement.params()))
        .flatMap(updateResult -> findUserById(user.getId()));
  }

  protected Single<User> updateUser(User user) {
    Statement<JsonArray> updateUserStatement = userStatements.update(user);
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxUpdateWithParams(
                    updateUserStatement.sql(), updateUserStatement.params()))
        .map(updateResult -> user);
  }

  protected Single<User> findUserById(String id) {
    Statement<JsonArray> findUserByIdStatement = userStatements.findById(id);
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxQueryWithParams(
                    findUserByIdStatement.sql(), findUserByIdStatement.params()))
        .map(ParserUtils::toUser);
  }

  protected Single<User> follow(User currentUser, User followedUser) {
    Statement<JsonArray> followStatement =
        followedUsersStatements.follow(currentUser.getId(), followedUser.getId());
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxUpdateWithParams(followStatement.sql(), followStatement.params()))
        .flatMap(updateResult -> Single.just(currentUser));
  }

  private static void dropDatabase(VertxTestContext vertxTestContext) {
    String dropDatabaseStatement = "DROP TABLE USERS; DROP TABLE FOLLOWED_USERS;";
    executeStatement(vertxTestContext, dropDatabaseStatement);
  }

  private void clearDatabase(VertxTestContext vertxTestContext) {
    String clearDatabaseStatement =
        "SET FOREIGN_KEY_CHECKS = 0; DELETE FROM USERS; DELETE FROM FOLLOWED_USERS; SET FOREIGN_KEY_CHECKS = 1;";
    executeStatement(vertxTestContext, clearDatabaseStatement);
  }

  private static void executeStatement(VertxTestContext testContext, String sql) {
    SQLClientHelper.inTransactionCompletable(
            jdbcClient, sqlConnection -> sqlConnection.rxExecute(sql))
        .subscribe(testContext::completeNow);
  }

  protected Buffer toBuffer(Object value) {
    return Buffer.buffer(writeValueAsString(value));
  }

  protected String writeValueAsString(Object value) {
    String result;
    try {
      result = wrapUnwrapRootValueObjectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  protected <T> T readValue(String value, Class<T> clazz) {
    T result;
    try {
      result = wrapUnwrapRootValueObjectMapper.readValue(value, clazz);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }
}
