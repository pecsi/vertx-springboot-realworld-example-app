package com.example.realworld.infrastructure.persistence;

import io.vertx.ext.sql.ResultSet;

public class JDBCRepository {

  Long getCountFromResultSet(ResultSet resultSet) {
    return resultSet.getRows().get(0).getLong("COUNT(*)");
  }
}
