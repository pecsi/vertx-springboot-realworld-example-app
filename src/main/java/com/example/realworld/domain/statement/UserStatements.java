package com.example.realworld.domain.statement;

import com.example.realworld.domain.entity.persistent.User;
import io.vertx.core.json.JsonArray;

public interface UserStatements {

  Statement<JsonArray> create(User user);

  Statement<JsonArray> update(User user);

  Statement<JsonArray> findById(Long id);

  Statement<JsonArray> existBy(String field, String value);

  Statement<JsonArray> existBy(String field, String value, Long excludeId);

  Statement<JsonArray> findByEmail(String email);
}
