package com.example.realworld.domain.utils;

import com.example.realworld.domain.entity.persistent.User;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.Optional;

public class ParserUtils {

  public static Optional<User> toUser(ResultSet resultSet) {
    User user = null;

    if (resultSet.getRows().size() > 0) {
      JsonObject row = resultSet.getRows().get(0);
      user = new User();
      user.setId(row.getLong("ID"));
      user.setUsername(row.getString("USERNAME"));
      user.setBio(row.getString("BIO"));
      user.setImage(row.getString("IMAGE"));
      user.setPassword(row.getString("PASSWORD"));
      user.setEmail(row.getString("EMAIL"));
      user.setToken(row.getString("TOKEN"));
    }

    return user != null ? Optional.of(user) : Optional.empty();
  }
}
