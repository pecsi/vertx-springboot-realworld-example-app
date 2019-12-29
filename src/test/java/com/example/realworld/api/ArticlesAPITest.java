package com.example.realworld.api;

import com.example.realworld.RealworldDataIntegrationTest;
import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.user.model.User;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static com.example.realworld.constants.TestsConstants.API_PREFIX;
import static com.example.realworld.constants.TestsConstants.HOST;
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
        .flatMap(createdLoggedUser -> createUser(userFollowed).map(user -> createdLoggedUser))
        .subscribe(user -> vertxTestContext.completeNow());
  }

  private List<Article> createArticlesFor(
      User author, String title, String description, String body, int quantity) {

    List<Article> articles = new LinkedList<>();

    for (int articleIndex = 0; articleIndex < quantity; articleIndex++) {

      String indexIdentifier = "_" + articleIndex;

      articles.add(
          createArticle(
              author,
              title + indexIdentifier,
              description + indexIdentifier,
              body + indexIdentifier,
              ""));
    }

    return articles;
  }

  private Article createArticle(
      User author, String title, String description, String body, String slug) {
    Article article = new Article();
    article.setAuthor(author);
    article.setTitle(title);
    article.setDescription(description);
    article.setBody(body);
    article.setSlug(slug);
    article.setCreatedAt(LocalDateTime.now());
    return article;
  }
}
