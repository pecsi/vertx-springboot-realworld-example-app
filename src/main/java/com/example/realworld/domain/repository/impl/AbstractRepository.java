package com.example.realworld.domain.repository.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.serviceproxy.ServiceException;

public class AbstractRepository {

  public static final int CONNECTION_FAILURE_CODE = 1;
  public static final int CREATE_FAILURE_CODE = 2;
  private SQLClient sqlClient;

  public AbstractRepository(SQLClient sqlClient) {
    this.sqlClient = sqlClient;
  }

  protected void updateWithParams(
      String sql, JsonArray params, Handler<AsyncResult<Long>> handler) {
    createConnection(
        createConnectionAsyncResult -> {
          if (createConnectionAsyncResult.succeeded()) {
            SQLConnection sqlConnection = createConnectionAsyncResult.result();
            sqlConnection.updateWithParams(
                sql,
                params,
                createStatementAsyncResult -> {
                  if (createStatementAsyncResult.succeeded()) {
                    Long id = createStatementAsyncResult.result().getKeys().getLong(0);
                    handler.handle(Future.succeededFuture(id));
                  } else {
                    handler.handle(
                        ServiceException.fail(
                            CREATE_FAILURE_CODE, createStatementAsyncResult.cause().getMessage()));
                  }
                });
          }
        });
  }

  private void createConnection(Handler<AsyncResult<SQLConnection>> handler) {
    this.sqlClient.getConnection(
        sqlConnectionAsyncResult -> {
          if (sqlConnectionAsyncResult.succeeded()) {
            handler.handle(Future.succeededFuture(sqlConnectionAsyncResult.result()));
          } else {
            handler.handle(
                ServiceException.fail(
                    CONNECTION_FAILURE_CODE, sqlConnectionAsyncResult.cause().getMessage()));
          }
        });
  }
}
