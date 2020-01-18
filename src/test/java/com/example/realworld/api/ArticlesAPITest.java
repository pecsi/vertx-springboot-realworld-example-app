package com.example.realworld.api;

import com.example.realworld.RealworldDataIntegrationTest;
import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.user.model.User;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    saveUsers(loggedUser, userFollowed);
    follow(loggedUser, userFollowed);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    createArticle(userFollowed, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    webClient
        .get(port, HOST, FEED_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
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
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(articlesResponse.getArticles().size(), is(5));
                          assertThat(articlesResponse.getArticlesCount(), is(10L));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      given50PersistedArticles_whenExecuteGetArticlesEndpointWithOffset0AndLimit10_shouldReturnListOf10Articles(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author1 = new User();
    author1.setUsername("author1");
    author1.setEmail("author1@mail.com");
    author1.setPassword("author1_123");

    User author2 = new User();
    author2.setUsername("author2");
    author2.setEmail("author2@mail.com");
    author2.setPassword("author2_123");

    User author3 = new User();
    author3.setUsername("author3");
    author3.setEmail("author3@mail.com");
    author3.setPassword("author3_123");

    User author4 = new User();
    author4.setUsername("author4");
    author4.setEmail("author4@mail.com");
    author4.setPassword("author4_123");

    User author5 = new User();
    author5.setUsername("author5");
    author5.setEmail("author5@mail.com");
    author5.setPassword("author5_123");

    saveUsers(loggedUser, author1, author2, author3, author4, author5);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    createArticle(author1, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    createArticle(author2, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    createArticle(author3, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    createArticle(author4, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    createArticle(author5, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    webClient
        .get(port, HOST, ARTICLES_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .addQueryParam("offset", "0")
        .addQueryParam("limit", "10")
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticlesResponse articlesResponse =
                              readValue(response.body(), ArticlesResponse.class, false);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(articlesResponse.getArticles().size(), is(10));
                          assertThat(articlesResponse.getArticlesCount(), is(50L));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      given45PersistedArticles_whenExecuteGetArticlesEndpointWithOffset0AndLimit10AndTagQueryParam_shouldReturnListOf5Articles(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author1 = new User();
    author1.setUsername("author1");
    author1.setEmail("author1@mail.com");
    author1.setPassword("author1_123");

    User author2 = new User();
    author2.setUsername("author2");
    author2.setEmail("author2@mail.com");
    author2.setPassword("author2_123");

    User author3 = new User();
    author3.setUsername("author3");
    author3.setEmail("author3@mail.com");
    author3.setPassword("author3_123");

    User author4 = new User();
    author4.setUsername("author4");
    author4.setEmail("author4@mail.com");
    author4.setPassword("author4_123");

    User author5 = new User();
    author5.setUsername("author5");
    author5.setEmail("author5@mail.com");
    author5.setPassword("author5_123");

    saveUsers(loggedUser, author1, author2, author3, author4, author5);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    createArticle(author1, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author2, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author3, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author4, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author5, "title", "description", "body", 5, Arrays.asList(tag1, tag2));

    webClient
        .get(port, HOST, ARTICLES_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .addQueryParam("offset", "0")
        .addQueryParam("limit", "10")
        .addQueryParam("tag", tag2.getName())
        //        .addQueryParam("author", userFollowed.getUsername())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticlesResponse articlesResponse =
                              readValue(response.body(), ArticlesResponse.class, false);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(articlesResponse.getArticles().size(), is(5));
                          assertThat(articlesResponse.getArticlesCount(), is(5L));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      given38PersistedArticles_whenExecuteGetArticlesEndpointWithOffset0AndLimit10AndAuthorQueryParam_shouldReturnListOf8Articles(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author1 = new User();
    author1.setUsername("author1");
    author1.setEmail("author1@mail.com");
    author1.setPassword("author1_123");

    User author2 = new User();
    author2.setUsername("author2");
    author2.setEmail("author2@mail.com");
    author2.setPassword("author2_123");

    User author3 = new User();
    author3.setUsername("author3");
    author3.setEmail("author3@mail.com");
    author3.setPassword("author3_123");

    User author4 = new User();
    author4.setUsername("author4");
    author4.setEmail("author4@mail.com");
    author4.setPassword("author4_123");

    User author5 = new User();
    author5.setUsername("author5");
    author5.setEmail("author5@mail.com");
    author5.setPassword("author5_123");

    saveUsers(loggedUser, author1, author2, author3, author4, author5);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    createArticle(author1, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author2, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author3, "title", "description", "body", 4, Collections.singletonList(tag1));

    createArticle(author4, "title", "description", "body", 4, Collections.singletonList(tag1));

    createArticle(author5, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    webClient
        .get(port, HOST, ARTICLES_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .addQueryParam("offset", "0")
        .addQueryParam("limit", "10")
        .addQueryParam("author", author3.getUsername())
        .addQueryParam("author", author4.getUsername())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticlesResponse articlesResponse =
                              readValue(response.body(), ArticlesResponse.class, false);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(articlesResponse.getArticles().size(), is(8));
                          assertThat(articlesResponse.getArticlesCount(), is(8L));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      given50PersistedArticles_whenExecuteGetArticlesEndpointWithOffset0AndLimit20AndFavoritedQueryParam_shouldReturnListOf20Articles(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author1 = new User();
    author1.setUsername("author1");
    author1.setEmail("author1@mail.com");
    author1.setPassword("author1_123");

    User author2 = new User();
    author2.setUsername("author2");
    author2.setEmail("author2@mail.com");
    author2.setPassword("author2_123");

    User author3 = new User();
    author3.setUsername("author3");
    author3.setEmail("author3@mail.com");
    author3.setPassword("author3_123");

    User author4 = new User();
    author4.setUsername("author4");
    author4.setEmail("author4@mail.com");
    author4.setPassword("author4_123");

    User author5 = new User();
    author5.setUsername("author5");
    author5.setEmail("author5@mail.com");
    author5.setPassword("author5_123");

    saveUsers(loggedUser, author1, author2, author3, author4, author5);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    List<Article> author1Articles =
        createArticle(author1, "title", "description", "body", 10, Collections.singletonList(tag1));

    List<Article> author2Articles =
        createArticle(author2, "title", "description", "body", 10, Collections.singletonList(tag1));

    List<Article> author3Articles =
        createArticle(author3, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author4, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author5, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    favorite(author4, author1Articles);

    favorite(author1, author2Articles);

    favorite(author5, author3Articles);

    webClient
        .get(port, HOST, ARTICLES_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .addQueryParam("offset", "0")
        .addQueryParam("limit", "20")
        .addQueryParam("favorited", author1.getUsername())
        .addQueryParam("favorited", author4.getUsername())
        .addQueryParam("favorited", author5.getUsername())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticlesResponse articlesResponse =
                              readValue(response.body(), ArticlesResponse.class, false);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(articlesResponse.getArticles().size(), is(20));
                          assertThat(articlesResponse.getArticlesCount(), is(30L));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      given70PersistedArticles_whenExecuteGetArticlesEndpointWithOffset0AndLimit10AndTagAndAuthorAndFavoritedQueryParams_shouldReturnListOf10Articles(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author1 = new User();
    author1.setUsername("author1");
    author1.setEmail("author1@mail.com");
    author1.setPassword("author1_123");

    User author2 = new User();
    author2.setUsername("author2");
    author2.setEmail("author2@mail.com");
    author2.setPassword("author2_123");

    User author3 = new User();
    author3.setUsername("author3");
    author3.setEmail("author3@mail.com");
    author3.setPassword("author3_123");

    User author4 = new User();
    author4.setUsername("author4");
    author4.setEmail("author4@mail.com");
    author4.setPassword("author4_123");

    User author5 = new User();
    author5.setUsername("author5");
    author5.setEmail("author5@mail.com");
    author5.setPassword("author5_123");

    saveUsers(loggedUser, author1, author2, author3, author4, author5);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    List<Article> author1Articles =
        createArticle(author1, "title", "description", "body", 10, Collections.singletonList(tag1));

    List<Article> author2Articles =
        createArticle(author2, "title", "description", "body", 10, Collections.singletonList(tag1));

    List<Article> author3Articles =
        createArticle(author3, "title", "description", "body", 30, Collections.singletonList(tag1));

    createArticle(author4, "title", "description", "body", 10, Collections.singletonList(tag1));

    createArticle(author5, "title", "description", "body", 10, Arrays.asList(tag1, tag2));

    favorite(author4, author1Articles);

    favorite(author1, author2Articles);

    favorite(author5, author3Articles);

    webClient
        .get(port, HOST, ARTICLES_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .addQueryParam("offset", "0")
        .addQueryParam("limit", "10")
        .addQueryParam("tag", tag1.getName())
        .addQueryParam("author", author3.getUsername())
        .addQueryParam("favorited", author5.getUsername())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticlesResponse articlesResponse =
                              readValue(response.body(), ArticlesResponse.class, false);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(articlesResponse.getArticles().size(), is(10));
                          assertThat(articlesResponse.getArticlesCount(), is(30L));
                          vertxTestContext.completeNow();
                        })));
  }
}
