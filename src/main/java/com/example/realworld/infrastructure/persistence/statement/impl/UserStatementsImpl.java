package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.domain.user.model.User;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.statement.UserStatements;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class UserStatementsImpl implements UserStatements {

  @Override
  public Statement<JsonArray> create(User user) {

    String sql =
        "INSERT INTO USERS (ID, USERNAME, BIO, EMAIL, IMAGE, PASSWORD, TOKEN) VALUES (?, ?, ?, ?, ?, ?, ?)";

    JsonArray params =
        new JsonArray()
            .add(user.getId())
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

    addFieldIfPresent(fields, params, user.getUsername(), "USERNAME = ?");

    addFieldIfPresent(fields, params, user.getBio(), "BIO = ?");

    addFieldIfPresent(fields, params, user.getEmail(), "EMAIL = ?");

    addFieldIfPresent(fields, params, user.getImage(), "IMAGE = ?");

    addFieldIfPresent(fields, params, user.getPassword(), "PASSWORD = ?");

    addFieldIfPresent(fields, params, user.getToken(), "TOKEN = ?");

    params.add(user.getId());

    String sql = "UPDATE USERS SET " + String.join(", ", fields) + " WHERE ID = ?";

    return new JsonArrayStatement(sql, params);
  }

  private void addFieldIfPresent(
      List<String> fields, JsonArray params, String fieldValue, String fieldExpression) {
    if (isPresent(fieldValue)) {
      fields.add(fieldExpression);
      params.add(fieldValue);
    }
  }

  @Override
  public Statement<JsonArray> findById(String id) {

    String sql = "SELECT * FROM USERS WHERE ID = ?";

    JsonArray params = new JsonArray().add(id);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> countBy(String field, String value) {

    String sql =
        String.format("SELECT COUNT(*) FROM USERS WHERE UPPER(%s) = ?", field.toUpperCase());

    JsonArray params = new JsonArray().add(value.toUpperCase().trim());

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> countBy(String field, String value, String excludeId) {

    String sql =
        String.format(
            "SELECT COUNT(*) FROM USERS WHERE UPPER(%s) = ? AND ID <> ?", field.toUpperCase());

    JsonArray params = new JsonArray().add(value.toUpperCase().trim()).add(excludeId);

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> findByEmail(String email) {

    String sql = "SELECT * FROM USERS WHERE UPPER(EMAIL) = ?";

    JsonArray params = new JsonArray().add(email.toUpperCase().trim());

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> findByUsername(String username) {

    String sql = "SELECT * FROM USERS WHERE UPPER(USERNAME) = ?";

    JsonArray params = new JsonArray().add(username.toUpperCase());

    return new JsonArrayStatement(sql, params);
  }

  private boolean isPresent(String value) {
    return value != null && !value.isEmpty();
  }
}
