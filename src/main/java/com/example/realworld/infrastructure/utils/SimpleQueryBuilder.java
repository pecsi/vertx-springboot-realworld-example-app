package com.example.realworld.infrastructure.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SimpleQueryBuilder {

  private List<String> queryStatements;
  private List<String> whereStatements;
  private String afterWhereStatement;

  public SimpleQueryBuilder() {
    this.queryStatements = new LinkedList<>();
    this.whereStatements = new LinkedList<>();
  }

  public void addQueryStatement(String queryStatement) {
    this.queryStatements.add(queryStatement);
  }

  public void updateQueryStatementConditional(String queryStatement, String whereStatement) {
    if (Objects.nonNull(queryStatement)) {
      queryStatements.add(queryStatement);
    }
    if (Objects.nonNull(whereStatement)) {
      whereStatements.add(whereStatement);
    }
  }

  public void addAfterWhereStatement(String afterWhereStatement) {
    this.afterWhereStatement = afterWhereStatement;
  }

  public String toQueryString() {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append(String.join(" ", queryStatements));
    if (!whereStatements.isEmpty()) {
      queryBuilder.append(" WHERE ");
      queryBuilder.append(String.join(" AND ", whereStatements));
    }
    if (afterWhereStatement != null && !afterWhereStatement.isEmpty()) {
      queryBuilder.append(" ").append(afterWhereStatement);
    }

    return queryBuilder.toString();
  }
}
