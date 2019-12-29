package com.example.realworld.api;

import com.example.realworld.RealworldDataIntegrationTest;
import com.example.realworld.domain.user.model.User;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.example.realworld.constants.TestsConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(VertxExtension.class)
public class ArticlesAPITest extends RealworldDataIntegrationTest {

  private final String ARTICLES_PATH = API_PREFIX + "/articles";
  private final String FEED_PATH = ARTICLES_PATH + "/feed";

  @Test
  public void shouldReturn401WhenExecuteFeedOperationWithoutAuthorizationHeader(
      VertxTestContext vertxTestContext) {

    webClient
        .get(port, HOST, FEED_PATH)
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          assertThat(
                              response.statusCode(), is(HttpResponseStatus.UNAUTHORIZED.code()));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      given10ArticlesForLoggedUser_whenExecuteFeedEndpointWithOffset0AndLimit5_shouldReturnListOf5Articles(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User userFollowed = new User();
    userFollowed.setUsername("userFollowed");
    userFollowed.setEmail("userFollowed@mail.com");
    userFollowed.setPassword("userFollowed_123");

    createUser(loggedUser)
        .flatMap(
            createdLoggedUser ->
                createUser(userFollowed)
                    .flatMap(
                        createdUserFollowed ->
                            follow(createdLoggedUser, createdUserFollowed)
                                .flatMap(
                                    createdLoggedUserWithFollow ->
                                        Single.create(
                                            (SingleEmitter<User> singleEmitter) ->
                                                createArticles(
                                                        createdUserFollowed,
                                                        "title",
                                                        "description",
                                                        "body",
                                                        10)
                                                    .subscribe(
                                                        article -> {},
                                                        vertxTestContext::failNow,
                                                        () ->
                                                            singleEmitter.onSuccess(
                                                                createdLoggedUser))))))
        .subscribe(
            createdUserFollowed ->
                webClient
                    .get(port, HOST, FEED_PATH)
                    .putHeader(
                        AUTHORIZATION_HEADER,
                        AUTHORIZATION_HEADER_VALUE_PREFIX + createdUserFollowed.getToken())
                    .addQueryParam("offset", "0")
                    .addQueryParam("limit", "5")
                    .as(BodyCodec.string())
                    .send(
                        vertxTestContext.succeeding(
                            response ->
                                vertxTestContext.verify(
                                    () -> {
                                      ArticlesResponse articlesResponse =
                                          readValue(response.body(), ArticlesResponse.class, false);
                                      assertThat(
                                          response.statusCode(), is(HttpResponseStatus.OK.code()));
                                      assertThat(articlesResponse.getArticles().size(), is(5));
                                      assertThat(articlesResponse.getArticlesCount(), is(10L));
                                      vertxTestContext.completeNow();
                                    }))));
  }
}
