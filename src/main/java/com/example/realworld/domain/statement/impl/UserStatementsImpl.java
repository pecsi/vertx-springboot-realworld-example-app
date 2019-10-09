package com.example.realworld.domain.statement.impl;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.statement.Statement;
import com.example.realworld.domain.statement.UserStatements;
import io.vertx.core.json.JsonArray;

import java.util.LinkedList;
import java.util.List;

public class UserStatementsImpl implements UserStatements {

  @Override
  public Statement<JsonArray> create(User user) {

    String sql =
        "INSERT INTO USERS (USERNAME, BIO, EMAIL, IMAGE, PASSWORD, TOKEN) VALUES (?, ?, ?, ?, ?, ?)";

    JsonArray params =
        new JsonArray()
            .add(user.getUsername())
            .add(user.getBio())
            .add(user.getEmail())
            .add(user.getImage())
            .add(user.getPassword())
            .add(user.getToken());

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> update(User user) {

    List<String> fields = new LinkedList<>();
    JsonArray params = new JsonArray();

    if (isPresent(user.getUsername())) {
      fields.add("USERNAME = ?");
      params.add(user.getUsername());
    }

    if (isPresent(user.getBio())) {
      fields.add("BIO = ?");
      params.add(user.getBio());
    }

    if (isPresent(user.getEmail())) {
      fields.add("EMAIL = ?");
      params.add(user.getEmail());
    }

    if (isPresent(user.getImage())) {
      fields.add("IMAGE = ?");
      params.add(user.getImage());
    }

    if (isPresent(user.getPassword())) {
      fields.add("PASSWORD = ?");
      params.add(user.getPassword());
    }

    if (isPresent(user.getToken())) {
      fields.add("TOKEN = ?");
      params.add(user.getToken());
    }

    params.add(user.getId());

    String sql = "UPDATE USERS SET " + String.join(", ", fields) + " WHERE ID = ?";

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> findById(Long id) {

    String sql = "SELECT * FROM USERS WHERE ID = ?";

    JsonArray params = new JsonArray().add(id);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> existBy(String field, String value) {

    String sql =
        String.format("SELECT COUNT(*) FROM USERS WHERE UPPER(%s) = ?", field.toUpperCase());

    JsonArray params = new JsonArray().add(value.toUpperCase().trim());

    return new JsonArrayStatement(sql, params);
  }

  private boolean isPresent(String value) {
    return value != null && !value.isEmpty();
  }
}
