package com.example.realworld.domain.statement.impl;

import com.example.realworld.domain.statement.Statement;
import io.vertx.core.json.JsonArray;

public class JsonArrayStatement implements Statement<JsonArray> {

  private String sql;
  private JsonArray params;

  public JsonArrayStatement(String sql, JsonArray params) {
    this.sql = sql;
    this.params = params;
  }

  @Override
  public String sql() {
    return sql;
  }

  @Override
  public JsonArray params() {
    return params;
  }
}
