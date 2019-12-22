package com.example.realworld.infrastructure.persistence.utils;

import com.example.realworld.domain.user.model.User;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.Optional;

public class ParserUtils {

  public static Optional<User> toUserOptional(ResultSet resultSet) {
    if (resultSet.getRows().size() > 0) {
      return Optional.of(getUserFromResultSet(resultSet));
    }
    return Optional.empty();
  }

  public static User toUser(ResultSet resultSet) {
    if (resultSet.getRows().size() > 0) {
      return getUserFromResultSet(resultSet);
    }
    return null;
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
}
