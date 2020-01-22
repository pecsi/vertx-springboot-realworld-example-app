package com.example.realworld.infrastructure.persistence.statement.impl;

import io.vertx.core.json.JsonArray;

import java.util.List;

public class AbstractStatements {

  protected void addFieldIfPresent(
      List<String> fields, JsonArray params, String fieldValue, String fieldExpression) {
    if (isPresent(fieldValue)) {
      fields.add(fieldExpression);
      params.add(fieldValue);
    }
  }

  private boolean isPresent(String value) {
    return value != null && !value.isEmpty();
  }
}
