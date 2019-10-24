package com.example.realworld.infrastructure.verticles;

import com.example.realworld.constants.TestsConstants;
import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.infrastructure.web.model.request.NewUserRequest;
import com.example.realworld.infrastructure.web.model.response.ErrorResponse;
import com.example.realworld.infrastructure.web.model.response.UserResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.example.realworld.constants.TestsConstants.API_PREFIX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(VertxExtension.class)
public class UsersAPIVerticleTest extends AbstractVerticleTest {

  private final String USERS_RESOURCE_PATH = API_PREFIX + "/users";
  private final String LOGIN_PATH = USERS_RESOURCE_PATH + "/login";

  @Test
  void shouldCreateAUser(VertxTestContext testContext) {

    NewUserRequest newUser = new NewUserRequest();
    newUser.setUsername("user");
    newUser.setEmail("user@mail.com");
    newUser.setPassword("user123");

    webClient
        .post(port, TestsConstants.HOST, USERS_RESOURCE_PATH)
        .as(BodyCodec.string())
        .sendBuffer(
            toBuffer(newUser),
            testContext.succeeding(
                response ->
                    testContext.verify(
                        () -> {
                          UserResponse result = readValue(response.body(), UserResponse.class);
                          assertThat(result.getUsername(), notNullValue());
                          assertThat(result.getEmail(), notNullValue());
                          assertThat(result.getToken(), notNullValue());
                          testContext.completeNow();
                        })));
  }

  @Test
  void shouldReturnConflictCodeWhenUsernameAlreadyExists(VertxTestContext testContext) {

    User user = new User();
    user.setUsername("user1");
    user.setEmail("user1@mail.com");
    user.setPassword("user1_123");

    createUser(user)
        .subscribe(
            persistedUser -> {
              NewUserRequest newUser = new NewUserRequest();
              newUser.setUsername(user.getUsername());
              newUser.setEmail("user2@mail.com");
              newUser.setPassword("user2_123");

              webClient
                  .post(port, TestsConstants.HOST, USERS_RESOURCE_PATH)
                  .as(BodyCodec.string())
                  .sendBuffer(
                      toBuffer(newUser),
                      testContext.succeeding(
                          response ->
                              testContext.verify(
                                  () -> {
                                    ErrorResponse result =
                                        readValue(response.body(), ErrorResponse.class);
                                    assertThat(
                                        response.statusCode(),
                                        is(HttpResponseStatus.CONFLICT.code()));
                                    assertThat(
                                        result.getBody(), contains("username already exists"));
                                    testContext.completeNow();
                                  })));
            });
  }

  @Test
  void shouldReturnConflictCodeWhenEmailAlreadyExists(VertxTestContext testContext) {

    User user = new User();
    user.setUsername("user1");
    user.setEmail("user1@mail.com");
    user.setPassword("user1_123");

    createUser(user)
        .subscribe(
            persistedUser -> {
              NewUserRequest newUser = new NewUserRequest();
              newUser.setUsername("user2");
              newUser.setEmail(user.getEmail());
              newUser.setPassword("user2_123");

              webClient
                  .post(port, TestsConstants.HOST, USERS_RESOURCE_PATH)
                  .as(BodyCodec.string())
                  .sendBuffer(
                      toBuffer(newUser),
                      testContext.succeeding(
                          response ->
                              testContext.verify(
                                  () -> {
                                    ErrorResponse result =
                                        readValue(response.body(), ErrorResponse.class);
                                    assertThat(
                                        response.statusCode(),
                                        is(HttpResponseStatus.CONFLICT.code()));
                                    assertThat(result.getBody(), contains("email already exists"));
                                    testContext.completeNow();
                                  })));
            });
  }
}
