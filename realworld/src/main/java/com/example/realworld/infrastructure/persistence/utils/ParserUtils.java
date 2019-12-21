package com.example.realworld.infrastructure.persistence.utils;

import com.example.realworld.domain.user.model.User;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

public class ParserUtils {

  public static User toUser(ResultSet resultSet) {
    User user = null;

    if (resultSet.getRows().size() > 0) {
      JsonObject row = resultSet.getRows().get(0);
      user = new User();
      user.setId(row.getString("ID"));
      user.setUsername(row.getString("USERNAME"));
      user.setBio(row.getString("BIO"));
      user.setImage(row.getString("IMAGE"));
      user.setPassword(row.getString("PASSWORD"));
      user.setEmail(row.getString("EMAIL"));
      user.setToken(row.getString("TOKEN"));
    }

    return user;
  }
}
