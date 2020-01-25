package com.example.realworld.api;

import com.example.realworld.RealworldDataIntegrationTest;
import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.Comment;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.user.model.User;
import com.example.realworld.infrastructure.web.model.request.NewArticleRequest;
import com.example.realworld.infrastructure.web.model.request.NewCommentRequest;
import com.example.realworld.infrastructure.web.model.request.UpdateArticleRequest;
import com.example.realworld.infrastructure.web.model.response.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.realworld.constants.TestsConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

  @Test
  public void
      givenAValidNewArticle_whenExecuteCreateArticleEndpoint_shouldReturnACreatedArticleWithStatusCode204(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    saveUser(loggedUser);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    NewArticleRequest newArticleRequest = new NewArticleRequest();
    newArticleRequest.setTitle("title");
    newArticleRequest.setDescription("description");
    newArticleRequest.setBody("body");
    newArticleRequest.setTagList(Arrays.asList(tag1.getName(), tag2.getName()));

    webClient
        .post(port, HOST, ARTICLES_PATH)
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .sendBuffer(
            toBuffer(newArticleRequest),
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticleResponse articleResponse =
                              readValue(response.body(), ArticleResponse.class, true);

                          assertThat(articleResponse.getSlug(), notNullValue());
                          assertThat(articleResponse.getTitle(), is(newArticleRequest.getTitle()));
                          assertThat(
                              articleResponse.getDescription(),
                              is(newArticleRequest.getDescription()));
                          assertThat(articleResponse.getBody(), is(newArticleRequest.getBody()));
                          assertThat(
                              articleResponse.getTagList(),
                              containsInAnyOrder(newArticleRequest.getTagList().toArray()));
                          assertThat(articleResponse.getCreatedAt(), notNullValue());
                          assertThat(articleResponse.getUpdatedAt(), notNullValue());
                          assertThat(articleResponse.isFavorited(), is(false));
                          assertThat(articleResponse.getFavoritesCount(), is(0L));
                          ProfileResponse author = articleResponse.getAuthor();
                          assertThat(author.getUsername(), is(loggedUser.getUsername()));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      givenAnExistentArticle_whenExecuteFindBySlugEndpoint_thenReturnAnArticleWithStatusCode200(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author = new User();
    author.setUsername("author");
    author.setEmail("author@mail.com");
    author.setPassword("author_123");

    saveUsers(loggedUser, author);

    follow(loggedUser, author);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(author);

    saveArticle(article);

    webClient
        .get(port, HOST, ARTICLES_PATH + "/" + article.getSlug())
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticleResponse articleResponse =
                              readValue(response.body(), ArticleResponse.class, true);

                          assertThat(articleResponse.getSlug(), notNullValue());
                          assertThat(articleResponse.getTitle(), is(article.getTitle()));
                          assertThat(
                              articleResponse.getDescription(), is(article.getDescription()));
                          assertThat(articleResponse.getBody(), is(article.getBody()));
                          assertThat(
                              articleResponse.getTagList(),
                              containsInAnyOrder(tag1.getName(), tag2.getName()));
                          assertThat(articleResponse.getCreatedAt(), notNullValue());
                          assertThat(articleResponse.getUpdatedAt(), notNullValue());
                          assertThat(articleResponse.isFavorited(), is(false));
                          assertThat(articleResponse.getFavoritesCount(), is(0L));
                          ProfileResponse authorResponse = articleResponse.getAuthor();
                          assertThat(authorResponse.getUsername(), is(author.getUsername()));
                          assertThat(authorResponse.isFollowing(), is(true));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      givenAnExistentArticle_whenExecuteUpdateArticleBySlugEndpoint_thenReturnAnUpdatedArticleWithStatusCode200(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    saveUsers(loggedUser);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(loggedUser);

    saveArticle(article);

    UpdateArticleRequest updateArticleRequest = new UpdateArticleRequest();
    updateArticleRequest.setTitle("new title");
    updateArticleRequest.setDescription("new description");
    updateArticleRequest.setBody("new body");

    webClient
        .put(port, HOST, ARTICLES_PATH + "/" + article.getSlug())
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .sendBuffer(
            toBuffer(updateArticleRequest),
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticleResponse articleResponse =
                              readValue(response.body(), ArticleResponse.class, true);

                          assertThat(articleResponse.getSlug(), is(article.getSlug()));
                          assertThat(
                              articleResponse.getTitle(), is(updateArticleRequest.getTitle()));
                          assertThat(
                              articleResponse.getDescription(),
                              is(updateArticleRequest.getDescription()));
                          assertThat(articleResponse.getBody(), is(updateArticleRequest.getBody()));
                          assertThat(
                              articleResponse.getTagList(),
                              containsInAnyOrder(tag1.getName(), tag2.getName()));
                          assertThat(articleResponse.getCreatedAt(), notNullValue());
                          assertThat(articleResponse.getUpdatedAt(), notNullValue());
                          assertThat(articleResponse.isFavorited(), is(false));
                          assertThat(articleResponse.getFavoritesCount(), is(0L));
                          ProfileResponse authorResponse = articleResponse.getAuthor();
                          assertThat(authorResponse.getUsername(), is(loggedUser.getUsername()));
                          assertThat(authorResponse.isFollowing(), is(false));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void givenAnExistentArticle_whenExecuteDeleteArticleBySlugEndpoint_thenReturnStatusCode200(
      VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User user = new User();
    user.setUsername("user");
    user.setEmail("user@mail.com");
    user.setPassword("user_123");

    saveUsers(loggedUser, user);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(loggedUser);

    saveArticle(article);

    favorite(user, article);

    webClient
        .delete(port, HOST, ARTICLES_PATH + "/" + article.getSlug())
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      givenAnExistentArticle_whenExecuteCreateCommentEndpoint_shouldReturnACreatedCommentWithStatusCode200(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author = new User();
    author.setUsername("author");
    author.setEmail("author@mail.com");
    author.setPassword("author_123");

    saveUsers(loggedUser, author);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(author);

    saveArticle(article);

    NewCommentRequest newCommentRequest = new NewCommentRequest();
    newCommentRequest.setBody("body");

    webClient
        .post(port, HOST, ARTICLES_PATH + "/" + article.getSlug() + "/comments")
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .sendBuffer(
            toBuffer(newCommentRequest),
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          CommentResponse commentResponse =
                              readValue(response.body(), CommentResponse.class, true);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(commentResponse.getId(), notNullValue());
                          assertThat(commentResponse.getCreatedAt(), notNullValue());
                          assertThat(commentResponse.getUpdatedAt(), notNullValue());
                          assertThat(commentResponse.getBody(), is(newCommentRequest.getBody()));
                          ProfileResponse commentResponseAuthor = commentResponse.getAuthor();
                          assertThat(
                              commentResponseAuthor.getUsername(), is(loggedUser.getUsername()));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      givenAnExistentArticleWithOneComment_whenExecuteDeleteCommentEndpoint_shouldReturnStatusCode200(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author = new User();
    author.setUsername("author");
    author.setEmail("author@mail.com");
    author.setPassword("author_123");

    saveUsers(loggedUser, author);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(author);

    saveArticle(article);

    Comment comment = new Comment();
    comment.setArticle(article);
    comment.setAuthor(loggedUser);
    comment.setBody("Body");

    saveComment(comment);

    webClient
        .delete(
            port, HOST, ARTICLES_PATH + "/" + article.getSlug() + "/comments/" + comment.getId())
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      givenAnExistentArticleWithFiveComments_whenExecuteGetCommentsEndpoint_shouldReturnArticleCommentListWithStatusCode200(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author = new User();
    author.setUsername("author");
    author.setEmail("author@mail.com");
    author.setPassword("author_123");

    saveUsers(loggedUser, author);

    follow(loggedUser, author);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(author);

    saveArticle(article);

    Comment comment1 = new Comment();
    comment1.setArticle(article);
    comment1.setAuthor(loggedUser);
    comment1.setBody("Comment 1");

    Comment comment2 = new Comment();
    comment2.setArticle(article);
    comment2.setAuthor(loggedUser);
    comment2.setBody("Comment 2");

    Comment comment3 = new Comment();
    comment3.setArticle(article);
    comment3.setAuthor(author);
    comment3.setBody("Comment 3");

    Comment comment4 = new Comment();
    comment4.setArticle(article);
    comment4.setAuthor(author);
    comment4.setBody("Comment 4");

    Comment comment5 = new Comment();
    comment5.setArticle(article);
    comment5.setAuthor(loggedUser);
    comment5.setBody("Comment 5");

    saveComments(comment1, comment2, comment3, comment4, comment5);

    webClient
        .get(port, HOST, ARTICLES_PATH + "/" + article.getSlug() + "/comments")
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          CommentsResponse commentsResponse =
                              readValue(response.body(), CommentsResponse.class, false);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(commentsResponse.getComments().size(), is(5));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      givenAnExistentArticleWithoutFavorites_whenExecuteFavoriteEndpoint_shouldReturnArticleFavoritedWithStatusCode200(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author = new User();
    author.setUsername("author");
    author.setEmail("author@mail.com");
    author.setPassword("author_123");

    saveUsers(loggedUser, author);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(author);

    saveArticle(article);

    webClient
        .post(port, HOST, ARTICLES_PATH + "/" + article.getSlug() + "/favorite")
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ArticleResponse articleResponse =
                              readValue(response.body(), ArticleResponse.class, true);
                          assertThat(response.statusCode(), is(HttpResponseStatus.OK.code()));
                          assertThat(articleResponse.getSlug(), notNullValue());
                          assertThat(articleResponse.getTitle(), notNullValue());
                          assertThat(articleResponse.getDescription(), notNullValue());
                          assertThat(articleResponse.getBody(), notNullValue());
                          assertThat(articleResponse.getTagList(), notNullValue());
                          assertThat(articleResponse.getCreatedAt(), notNullValue());
                          assertThat(articleResponse.getUpdatedAt(), notNullValue());
                          assertThat(articleResponse.isFavorited(), is(true));
                          assertThat(articleResponse.getFavoritesCount(), is(1L));
                          ProfileResponse articleResponseAuthor = articleResponse.getAuthor();
                          assertThat(articleResponseAuthor.getUsername(), is(author.getUsername()));
                          vertxTestContext.completeNow();
                        })));
  }

  @Test
  public void
      givenAnExistentArticleWithAlreadyFavoriteForLoggedUser_whenExecuteFavoriteEndpoint_shouldReturnStatusCode409(
          VertxTestContext vertxTestContext) {

    User loggedUser = new User();
    loggedUser.setUsername("loggedUser");
    loggedUser.setEmail("loggedUser@mail.com");
    loggedUser.setPassword("loggedUser_123");

    User author = new User();
    author.setUsername("author");
    author.setEmail("author@mail.com");
    author.setPassword("author_123");

    saveUsers(loggedUser, author);

    Tag tag1 = new Tag();
    tag1.setName("tag1");

    Tag tag2 = new Tag();
    tag2.setName("tag2");

    saveTags(tag1, tag2);

    Article article = new Article();
    article.setTitle("Title");
    article.setDescription("Description");
    article.setBody("Body");
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    article.setTags(Arrays.asList(tag1, tag2));
    article.setAuthor(author);

    saveArticle(article);

    favorite(loggedUser, article);

    webClient
        .post(port, HOST, ARTICLES_PATH + "/" + article.getSlug() + "/favorite")
        .putHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_VALUE_PREFIX + loggedUser.getToken())
        .as(BodyCodec.string())
        .send(
            vertxTestContext.succeeding(
                response ->
                    vertxTestContext.verify(
                        () -> {
                          ErrorResponse errorResponse =
                              readValue(response.body(), ErrorResponse.class, true);
                          assertThat(response.statusCode(), is(HttpResponseStatus.CONFLICT.code()));
                          assertThat(
                              errorResponse.getBody(),
                              containsInAnyOrder("article already favorited"));

                          vertxTestContext.completeNow();
                        })));
  }
}
