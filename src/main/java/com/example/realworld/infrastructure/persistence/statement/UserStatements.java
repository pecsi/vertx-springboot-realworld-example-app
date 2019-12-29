package com.example.realworld.infrastructure.persistence.statement;

import com.example.realworld.domain.user.model.User;
import io.vertx.core.json.JsonArray;

public interface UserStatements {

  Statement<JsonArray> create(User user);

  Statement<JsonArray> update(User user);

  Statement<JsonArray> findById(String id);

  Statement<JsonArray> countBy(String field, String value);

  Statement<JsonArray> countBy(String field, String value, String exclusionId);

  Statement<JsonArray> findByEmail(String email);

  Statement<JsonArray> findByUsername(String username);
}
