package com.example.realworld.infrastructure.persistence.utils;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.tag.model.Tag;
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
    return parseResultSet(resultSet, ParserUtils::getArticlesFromResultSet, LinkedList::new);
  }

  public static List<Tag> toTagList(ResultSet resultSet) {
    return parseResultSet(resultSet, ParserUtils::getTagsFromResultSet, LinkedList::new);
  }

  public static Optional<Tag> toTagOptional(ResultSet resultSet) {
    return parseResultSet(resultSet, ParserUtils::getTagOptionalFromResultSet, Optional::empty);
  }

  private static Optional<Tag> getTagOptionalFromResultSet(ResultSet resultSet) {
    return Optional.of(getTagFromResultSet(resultSet));
  }

  private static List<Tag> getTagsFromResultSet(ResultSet resultSet) {
    List<JsonObject> rows = resultSet.getRows();
    return rows.stream().map(ParserUtils::getTagFromRow).collect(Collectors.toList());
  }

  public static Optional<Article> toArticleOptional(ResultSet resultSet) {
    JsonObject row = resultSet.getRows().get(0);
    return Optional.of(ParserUtils.getArticleFromRow(row));
  }

  private static List<Article> getArticlesFromResultSet(ResultSet resultSet) {
    List<JsonObject> rows = resultSet.getRows();
    return rows.stream().map(ParserUtils::getArticleFromRow).collect(Collectors.toList());
  }

  private static Article getArticleFromRow(JsonObject row) {
    Article article = new Article();
    article.setId(row.getString("ID"));
    article.setTitle(row.getString("TITLE"));
    article.setDescription(row.getString("DESCRIPTION"));
    article.setBody(row.getString("BODY"));
    article.setSlug(row.getString("SLUG"));
    article.setCreatedAt(fromTimestamp(row.getString("CREATED_AT")));
    article.setUpdatedAt(fromTimestamp(row.getString("UPDATED_AT")));
    User author = new User();
    author.setId(row.getString("AUTHOR_ID"));
    author.setUsername(row.getString("AUTHOR_USERNAME"));
    article.setAuthor(author);
    return article;
  }

  private static Optional<User> getUserOptionalFromResultSet(ResultSet resultSet) {
    return Optional.of(getUserFromResultSet(resultSet));
  }

  private static Tag getTagFromResultSet(ResultSet resultSet) {
    JsonObject row = resultSet.getRows().get(0);
    return getTagFromRow(row);
  }

  private static Tag getTagFromRow(JsonObject row) {
    Tag tag = new Tag();
    tag.setId(row.getString("ID"));
    tag.setName(row.getString("NAME"));
    return tag;
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

  public static String format(LocalDateTime localDateTime, String pattern) {
    return localDateTime != null
        ? localDateTime.format(DateTimeFormatter.ofPattern(pattern))
        : null;
  }
}
