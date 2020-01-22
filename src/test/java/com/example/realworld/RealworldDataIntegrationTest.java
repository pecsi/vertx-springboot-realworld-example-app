package com.example.realworld;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.Comment;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.user.model.User;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.buffer.Buffer;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RealworldDataIntegrationTest extends RealworldApplicationDatabaseIntegrationTest {

  @AfterEach
  public void afterEach(VertxTestContext vertxTestContext) {
    clearDatabase();
    vertxTestContext.completeNow();
  }

  protected void saveUsers(User... users) {
    for (User user : users) {
      saveUser(user);
    }
  }

  protected void saveUser(User user) {
    user.setId(UUID.randomUUID().toString());
    user.setPassword(hashProvider.hashPassword(user.getPassword()));
    user.setToken(tokenProvider.generateToken(user.getId()));
    String sql =
        String.format(
            "INSERT INTO USERS (ID, USERNAME, BIO, IMAGE, PASSWORD, EMAIL, TOKEN) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
            user.getId(),
            user.getUsername(),
            user.getBio(),
            user.getImage(),
            user.getPassword(),
            user.getEmail(),
            user.getToken());
    executeSql(sql);
  }

  protected void follow(User currentUser, User followedUser) {
    String sql =
        String.format(
            "INSERT INTO USERS_FOLLOWED (USER_ID, FOLLOWED_ID) VALUES ('%s', '%s')",
            currentUser.getId(), followedUser.getId());
    executeSql(sql);
  }

  protected void saveTags(Tag... tags) {
    StringBuilder builder = new StringBuilder();

    for (Tag tag : tags) {
      tag.setId(UUID.randomUUID().toString());
      String sql =
          String.format(
              "INSERT INTO TAGS (ID, NAME) VALUES ('%s', '%s');", tag.getId(), tag.getName());
      builder.append(sql);
    }

    executeSql(builder.toString());
  }

  protected List<Article> createArticle(
      User author, String title, String description, String body, int quantity, List<Tag> tags) {
    List<Article> articles = new LinkedList<>();

    LocalDateTime now = LocalDateTime.now();

    for (int articleIndex = 0; articleIndex < quantity; articleIndex++) {
      String articleIndexIdentifier = "_" + articleIndex;
      Article article = new Article();

      article.setTitle(title + articleIndexIdentifier);
      article.setDescription(description + articleIndexIdentifier);
      article.setBody(body + articleIndexIdentifier);
      article.setAuthor(author);
      article.setTags(tags);
      LocalDateTime articleDate = now.plusDays(articleIndex);
      article.setCreatedAt(articleDate);
      article.setUpdatedAt(articleDate);
      articles.add(article);
      saveArticle(article);
    }

    return articles;
  }

  protected void saveArticles(Article... articles) {
    for (Article article : articles) {
      saveArticle(article);
    }
  }

  protected void saveArticle(Article article) {
    StringBuilder builder = new StringBuilder();

    article.setId(UUID.randomUUID().toString());
    article.setSlug(slugProvider.slugify(article.getTitle()));

    builder.append(
        String.format(
            "INSERT INTO ARTICLES (ID, TITLE, DESCRIPTION, BODY, SLUG, AUTHOR_ID, CREATED_AT, UPDATED_AT) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
            article.getId(),
            article.getTitle(),
            article.getDescription(),
            article.getBody(),
            article.getSlug(),
            article.getAuthor().getId(),
            ParserUtils.toTimestamp(article.getCreatedAt()),
            ParserUtils.toTimestamp(article.getUpdatedAt())));

    article
        .getTags()
        .forEach(
            tag ->
                builder.append(
                    String.format(
                        "INSERT INTO ARTICLES_TAGS (ARTICLE_ID, TAG_ID) VALUES ('%s', '%s');",
                        article.getId(), tag.getId())));

    executeSql(builder.toString());
  }

  protected void favorite(User user, Article article) {
    String sql =
        String.format(
            "INSERT INTO ARTICLES_USERS (ARTICLE_ID, USER_ID) VALUES ('%s','%s');",
            article.getId(), user.getId());
    executeSql(sql);
  }

  protected void saveComment(Comment comment) {
    comment.setId(UUID.randomUUID().toString());
    LocalDateTime now = LocalDateTime.now();
    comment.setCreatedAt(now);
    comment.setUpdatedAt(now);

    String sql =
        String.format(
            "INSERT INTO COMMENTS (ID, AUTHOR_ID, ARTICLE_ID, CREATED_AT, UPDATED_AT, BODY) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
            comment.getId(),
            comment.getAuthor().getId(),
            comment.getArticle().getId(),
            ParserUtils.toTimestamp(comment.getCreatedAt()),
            ParserUtils.toTimestamp(comment.getUpdatedAt()),
            comment.getBody());

    executeSql(sql);
  }

  protected void favorite(User user, List<Article> articles) {
    articles.forEach(article -> favorite(user, article));
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

  protected <T> T readValue(
      String value, Class<T> clazz, boolean userWrapUnwrapRootValueObjectMapper) {
    T result;
    try {
      result =
          userWrapUnwrapRootValueObjectMapper
              ? wrapUnwrapRootValueObjectMapper.readValue(value, clazz)
              : defaultObjectMapper.readValue(value, clazz);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }
}
