package com.example.realworld.infrastructure.verticles;

import com.example.realworld.constants.TestsConstants;
import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.infrastructure.web.model.request.LoginRequest;
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
  private final String USER_RESOURCE_PATH = API_PREFIX + "/user";
  private final String LOGIN_PATH = USERS_RESOURCE_PATH + "/login";

  @Test
  void shouldCreateAUser(VertxTestContext vertxTestContext) {

    NewUserRequest newUser = new NewUserRequest();
    newUser.setUsername("user");
    newUser.setEmail("user@mail.com");
    newUser.setPassword("user123");

    webClient
        .post(port, TestsConstants.HOST, USERS_RESOURCE_PATH)
        .as(BodyCodec.string())
        .sendBuffer(
            toBuffer(newUser),
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          UserResponse result = readValue(response.body(), UserResponse.class);
                          assertThat(result.getUsername(), notNullValue());
                          assertThat(result.getEmail(), notNullValue());
                          assertThat(result.getToken(), notNullValue());
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  void shouldReturnConflictCodeWhenUsernameAlreadyExists(VertxTestContext vertxTestContext) {

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
                      vertxTestContext.succeeding(
                          response ->
                              vertxTestContext.verify(
                                  () -> {
                                    ErrorResponse result =
                                        readValue(response.body(), ErrorResponse.class);
                                    assertThat(
                                        response.statusCode(),
                                        is(HttpResponseStatus.CONFLICT.code()));
                                    assertThat(
                                        result.getBody(), contains("username already exists"));
                                    vertxTestContext.completeNow();
                                  })));
            });
  }

  @Test
  void shouldReturnConflictCodeWhenEmailAlreadyExists(VertxTestContext vertxTestContext) {

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
                      vertxTestContext.succeeding(
                          response ->
                              vertxTestContext.verify(
                                  () -> {
                                    ErrorResponse result =
                                        readValue(response.body(), ErrorResponse.class);
                                    assertThat(
                                        response.statusCode(),
                                        is(HttpResponseStatus.CONFLICT.code()));
                                    assertThat(result.getBody(), contains("email already exists"));
                                    vertxTestContext.completeNow();
                                  })));
            });
  }

  @Test
  void shouldReturnUserOnValidLoginRequest(VertxTestContext vertxTestContext) {

    User user = new User();
    user.setUsername("user1");
    user.setEmail("user1@mail.com");
    user.setImage("image");
    user.setBio("bio");
    String userPassword = "user1_123";
    user.setPassword(userPassword);

    createUser(user)
        .subscribe(
            persistedUser -> {
              LoginRequest loginRequest = new LoginRequest();
              loginRequest.setEmail(persistedUser.getEmail());
              loginRequest.setPassword(userPassword);

              webClient
                  .post(port, TestsConstants.HOST, LOGIN_PATH)
                  .as(BodyCodec.string())
                  .sendBuffer(
                      toBuffer(loginRequest),
                      vertxTestContext.succeeding(
                          response ->
                              vertxTestContext.verify(
                                  () -> {
                                    UserResponse userResponse =
                                        readValue(response.body(), UserResponse.class);
                                    assertThat(userResponse.getUsername(), is(user.getUsername()));
                                    assertThat(userResponse.getEmail(), is(user.getEmail()));
                                    assertThat(userResponse.getBio(), is(user.getBio()));
                                    assertThat(userResponse.getImage(), is(user.getImage()));
                                    assertThat(
                                        userResponse.getToken(), is(not(persistedUser.getToken())));
                                    vertxTestContext.completeNow();
                                  })));
            });
  }

  @Test
  void shouldReturnUnauthorizedCodeWhenUserNotFound(VertxTestContext vertxTestContext) {

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("user@email.com");
    loginRequest.setPassword("user123");

    webClient
        .post(port, TestsConstants.HOST, LOGIN_PATH)
        .as(BodyCodec.string())
        .sendBuffer(
            toBuffer(loginRequest),
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ErrorResponse result = readValue(response.body(), ErrorResponse.class);
                          assertThat(
                              response.statusCode(), is(HttpResponseStatus.UNAUTHORIZED.code()));
                          assertThat(result.getBody(), contains("Unauthorized"));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  void shouldReturnUnauthorizedCodeWhenPasswordDoesNotMatch(VertxTestContext vertxTestContext) {

    User user = new User();
    user.setUsername("user1");
    user.setEmail("user1@mail.com");
    user.setPassword("user1_123");

    createUser(user)
        .subscribe(
            persistedUser -> {
              LoginRequest loginRequest = new LoginRequest();
              loginRequest.setEmail(persistedUser.getEmail());
              loginRequest.setPassword("user123");

              webClient
                  .post(port, TestsConstants.HOST, LOGIN_PATH)
                  .as(BodyCodec.string())
                  .sendBuffer(
                      toBuffer(loginRequest),
                      vertxTestContext.succeeding(
                          response ->
                              vertxTestContext.verify(
                                  () -> {
                                    ErrorResponse result =
                                        readValue(response.body(), ErrorResponse.class);
                                    assertThat(
                                        response.statusCode(),
                                        is(HttpResponseStatus.UNAUTHORIZED.code()));
                                    assertThat(result.getBody(), contains("Unauthorized"));
                                    vertxTestContext.completeNow();
                                  })));
            });
  }

  @Test
  public void shouldReturnUnauthorizedOnTryAccessUserGetResourceWithoutAnToken(
      VertxTestContext vertxTestContext) {

    webClient
        .get(port, TestsConstants.HOST, USER_RESOURCE_PATH)
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ErrorResponse errorResponse =
                              readValue(response.body(), ErrorResponse.class);
                          assertThat(
                              response.statusCode(), is(HttpResponseStatus.UNAUTHORIZED.code()));
                          assertThat(errorResponse.getBody(), contains("Unauthorized"));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void shouldReturnUserWithStatusCode200(VertxTestContext vertxTestContext) {

    User user = new User();
    user.setUsername("user1");
    user.setEmail("user1@mail.com");
    user.setImage("image");
    user.setBio("bio");
    String userPassword = "user1_123";
    user.setPassword(userPassword);

    createUser(user)
        .subscribe(
            persistedUser ->
                webClient
                    .get(port, TestsConstants.HOST, USER_RESOURCE_PATH)
                    .putHeader(
                        TestsConstants.AUTHORIZATION_HEADER,
                        TestsConstants.AUTHORIZATION_HEADER_VALUE_PREFIX + persistedUser.getToken())
                    .as(BodyCodec.string())
                    .send(
                        vertxTestContext.succeeding(
                            response ->
                                vertxTestContext.verify(
                                    () -> {
                                      UserResponse userResponse =
                                          readValue(response.body(), UserResponse.class);
                                      assertThat(
                                          userResponse.getUsername(),
                                          is(persistedUser.getUsername()));
                                      assertThat(
                                          userResponse.getEmail(), is(persistedUser.getEmail()));
                                      assertThat(userResponse.getBio(), is(persistedUser.getBio()));
                                      assertThat(
                                          userResponse.getImage(), is(persistedUser.getImage()));
                                      assertThat(
                                          userResponse.getToken(), is(persistedUser.getToken()));
                                      vertxTestContext.completeNow();
                                    }))));
  }
}
