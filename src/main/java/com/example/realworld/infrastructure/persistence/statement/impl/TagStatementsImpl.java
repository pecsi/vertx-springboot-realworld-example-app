package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.statement.TagStatements;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

@Component
public class TagStatementsImpl extends AbstractStatements implements TagStatements {
  @Override
  public Statement<JsonArray> store(Tag tag) {

    String sql = "INSERT INTO TAGS (ID, NAME) VALUES (?, ?)";

    JsonArray params = new JsonArray().add(tag.getId()).add(tag.getName());

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> countBy(String field, String value) {
    String sql =
        String.format("SELECT COUNT(*) FROM TAGS WHERE UPPER(%s) = ?", field.toUpperCase());

    JsonArray params = new JsonArray().add(value.toUpperCase().trim());

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> findTagByName(String name) {

    String sql = "SELECT * FROM TAGS WHERE UPPER(NAME) = ?";

    JsonArray params = new JsonArray().add(name.toUpperCase().trim());

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public String findAll() {
    return "SELECT * FROM TAGS";
  }
}
