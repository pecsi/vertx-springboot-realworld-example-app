package com.example.realworld.infrastructure.persistence.utils;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.user.model.User;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ParserUtils {

  private static DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public static Optional<User> toUserOptional(ResultSet resultSet) {
    return parseResultSet(resultSet, ParserUtils::getUserOptionalFromResultSet, Optional::empty);
  }

  public static User toUser(ResultSet resultSet) {
    return parseResultSet(resultSet, ParserUtils::getUserFromResultSet, () -> null);
  }

  public static List<Article> toArticleList(ResultSet resultSet) {
    return parseResultSet(resultSet, ParserUtils::getArticleFromResultSet, LinkedList::new);
  }

  private static List<Article> getArticleFromResultSet(ResultSet resultSet) {
    List<JsonObject> rows = resultSet.getRows();
    List<Article> articles =
        rows.stream()
            .map(
                row -> {
                  Article article = new Article();
                  article.setId(row.getString("ID"));
                  article.setTitle(row.getString("TITLE"));
                  article.setDescription(row.getString("DESCRIPTION"));
                  article.setBody(row.getString("BODY"));
                  article.setSlug(row.getString("SLUG"));
                  article.setCreatedAt(fromTimestamp(row.getString("CREATED_AT")));
                  article.setUpdatedAt(fromTimestamp(row.getString("UPDATED_AT")));
                  article.setAuthor(new User(row.getString("AUTHOR_USERNAME")));
                  return article;
                })
            .collect(Collectors.toList());
    return articles;
  }

  private static Optional<User> getUserOptionalFromResultSet(ResultSet resultSet) {
    return Optional.of(getUserFromResultSet(resultSet));
  }

  private static User getUserFromResultSet(ResultSet resultSet) {
    JsonObject row = resultSet.getRows().get(0);
    User user = new User();
    user.setId(row.getString("ID"));
    user.setUsername(row.getString("USERNAME"));
    user.setBio(row.getString("BIO"));
    user.setImage(row.getString("IMAGE"));
    user.setPassword(row.getString("PASSWORD"));
    user.setEmail(row.getString("EMAIL"));
    user.setToken(row.getString("TOKEN"));
    return user;
  }

  private static <T> T parseResultSet(
      ResultSet resultSet, Function<ResultSet, T> function, Supplier<T> suplier) {
    if (!resultSet.getRows().isEmpty()) {
      return function.apply(resultSet);
    }
    return suplier.get();
  }

  public static String toTimestamp(LocalDateTime localDateTime) {
    return localDateTime != null ? localDateTime.format(dateTimeFormatter) : null;
  }

  private static LocalDateTime fromTimestamp(String timestamp) {
    return timestamp != null ? LocalDateTime.parse(timestamp, dateTimeFormatter) : null;
  }
}
