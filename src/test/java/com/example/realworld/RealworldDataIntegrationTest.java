package com.example.realworld;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.NewArticle;
import com.example.realworld.domain.tag.model.NewTag;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.user.model.NewUser;
import com.example.realworld.domain.user.model.UpdateUser;
import com.example.realworld.domain.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RealworldDataIntegrationTest extends RealworldApplicationDatabaseIntegrationTest {

  @AfterEach
  public void afterEach(VertxTestContext vertxTestContext) {
    clearDatabase();
    vertxTestContext.completeNow();
  }

  protected Single<User> createUser(User user) {
    return createUser(toNewUser(user))
        .flatMap(createdUser -> updateUser(toUpdateUser(user), createdUser.getId()));
  }

  protected User createUserEntityManager(User user) {
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
    return user;
  }

  protected void followEntityManager(User currentUser, User followedUser) {
    String sql =
        String.format(
            "INSERT INTO USERS_FOLLOWED (USER_ID, FOLLOWED_ID) VALUES ('%s', '%s')",
            currentUser.getId(), followedUser.getId());
    executeSql(sql);
  }

  protected List<Tag> createTagsEntityManager(Tag... tags) {
    StringBuilder builder = new StringBuilder();

    for (Tag tag : tags) {
      tag.setId(UUID.randomUUID().toString());
      String sql =
          String.format(
              "INSERT INTO TAGS (ID, NAME) VALUES ('%s', '%s');", tag.getId(), tag.getName());
      builder.append(sql);
    }

    executeSql(builder.toString());

    return Arrays.asList(tags);
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

  protected Flowable<Article> createArticles(
      User author, String title, String description, String body, int quantity, List<Tag> tags) {
    List<NewArticle> newArticles =
        createArticlesFor(author, title, description, body, quantity, tags);
    return Flowable.fromIterable(newArticles)
        .flatMapSingle(article -> articleService.create(article));
  }

  protected Flowable<Tag> createTags(NewTag... newTags) {
    return Flowable.fromArray(newTags).flatMapSingle(newTag -> tagService.create(newTag));
  }

  private List<NewArticle> createArticlesFor(
      User author, String title, String description, String body, int quantity, List<Tag> tags) {
    List<NewArticle> newArticles = new LinkedList<>();

    for (int articleIndex = 0; articleIndex < quantity; articleIndex++) {
      String indexIdentifier = "_" + articleIndex;
      NewArticle newArticle = new NewArticle();
      newArticle.setAuthor(author);
      newArticle.setTitle(title + indexIdentifier);
      newArticle.setDescription(description + indexIdentifier);
      newArticle.setBody(body + indexIdentifier);

      List<NewTag> newTags = new LinkedList<>();
      for (Tag tag : tags) {
        NewTag newTag = new NewTag();
        newTag.setName(tag.getName());
        newTags.add(newTag);
      }

      newArticle.setTags(newTags);

      newArticles.add(newArticle);
    }

    return newArticles;
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
