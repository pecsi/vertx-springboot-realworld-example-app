package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.statement.Statement;
import com.example.realworld.domain.statement.UserStatements;
import com.example.realworld.domain.statement.impl.UserStatementsImpl;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.config.ObjectMapperConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class AbstractVerticleTest {

  protected WebClient webClient;
  protected static JsonObject config;
  protected static JDBCClient jdbcClient;
  protected static ObjectMapper objectMapper = ObjectMapperConfig.wrapUnwrapRootValueObjectMapper();
  protected static int port;
  protected UserStatements userStatements = new UserStatementsImpl();

  @BeforeAll
  public static void beforeAll(Vertx vertx, VertxTestContext testContext) {

    getConfig(vertx)
        .setHandler(
            getConfigAsyncResult -> {
              if (getConfigAsyncResult.succeeded()) {
                config = getConfigAsyncResult.result();
                port = config.getInteger(Constants.SERVER_PORT_KEY);
                jdbcClient =
                    JDBCClient.createNonShared(
                        vertx, config.getJsonObject(Constants.DATA_BASE_CONFIG_KEY));
                deployMainVerticle(vertx, testContext);
              } else {
                testContext.failNow(getConfigAsyncResult.cause());
              }
            });
  }

  @BeforeEach
  public void beforeEach(Vertx vertx) {
    configWebClient(vertx);
  }

  @AfterEach
  public void afterEach(VertxTestContext testContext) {
    SQLClientHelper.inTransactionCompletable(
            jdbcClient, sqlConnection -> sqlConnection.rxExecute("DELETE FROM USERS;"))
        .subscribe(testContext::completeNow);
  }

  private static void deployMainVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
        new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  private void configWebClient(Vertx vertx) {
    WebClientOptions webClientOptions = new WebClientOptions();

    webClient = WebClient.create(vertx);
  }

  private static Future<JsonObject> getConfig(Vertx vertx) {
    return Future.future(
        jsonObjectPromise -> {
          ConfigStoreOptions fileStore =
              new ConfigStoreOptions()
                  .setType("file")
                  .setConfig(new JsonObject().put("path", "conf/config.json"));
          ConfigRetrieverOptions configRetrieverOptions =
              new ConfigRetrieverOptions().addStore(fileStore);
          ConfigRetriever.create(vertx, configRetrieverOptions)
              .getConfig(
                  ar -> {
                    if (ar.succeeded()) {
                      jsonObjectPromise.complete(ar.result());
                    } else {
                      jsonObjectPromise.fail(ar.cause());
                    }
                  });
        });
  }

  protected Buffer toBuffer(Object value) {
    return Buffer.buffer(writeValueAsString(value));
  }

  protected String writeValueAsString(Object value) {
    String result;
    try {
      result = objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  protected <T> T readValue(String value, Class<T> clazz) {
    T result;
    try {
      result = objectMapper.readValue(value, clazz);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

  protected Single<User> createUser(User user) {
    Statement<JsonArray> createUserStatement = userStatements.create(user);
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxUpdateWithParams(
                    createUserStatement.sql(), createUserStatement.params()))
        .map(
            updateResult -> {
              user.setId(updateResult.getKeys().getLong(0));
              return user;
            });
  }
}
