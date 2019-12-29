package com.example.realworld;

import com.example.realworld.domain.user.model.NewUser;
import com.example.realworld.domain.user.model.UpdateUser;
import com.example.realworld.domain.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.Single;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;

public class RealworldDataIntegrationTest extends RealworldApplicationIntegrationTest {

  @AfterEach
  public void afterEach(VertxTestContext vertxTestContext) {
    clearDatabase(vertxTestContext);
  }

  protected Single<User> createUser(User user) {
    return createUser(toNewUser(user))
        .flatMap(createdUser -> updateUser(toUpdateUser(user), createdUser.getId()));
  }

  protected Single<User> createUser(NewUser newUser) {
    return userService.create(newUser);
  }

  protected Single<User> updateUser(UpdateUser updateUser, String exclusionId) {
    return userService.update(updateUser, exclusionId);
  }

  protected Single<User> follow(User currentUser, User followedUser) {
    return userService
        .follow(currentUser.getId(), followedUser.getId())
        .andThen(Single.just(currentUser));
  }

  private UpdateUser toUpdateUser(User createdUser) {
    UpdateUser updateUser = new UpdateUser();
    updateUser.setUsername(createdUser.getUsername());
    updateUser.setEmail(createdUser.getEmail());
    updateUser.setBio(createdUser.getBio());
    updateUser.setImage(createdUser.getImage());
    return updateUser;
  }

  private NewUser toNewUser(User user) {
    NewUser newUser = new NewUser();
    newUser.setUsername(user.getUsername());
    newUser.setEmail(user.getEmail());
    newUser.setPassword(user.getPassword());
    return newUser;
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
