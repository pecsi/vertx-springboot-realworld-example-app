package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.domain.statement.Statement;
import com.example.realworld.domain.statement.UserStatements;
import com.example.realworld.domain.statement.impl.UserStatementsImpl;
import com.example.realworld.domain.utils.ParserUtils;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.config.AuthProviderConfig;
import com.example.realworld.infrastructure.web.config.ObjectMapperConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class AbstractVerticleTest {

  protected WebClient webClient;
  protected static JWTAuth jwtAuth;
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
                jwtAuth = jwtAuth(vertx, config);
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
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxExecute(
                    "SET FOREIGN_KEY_CHECKS = 0; DELETE FROM USERS; SET FOREIGN_KEY_CHECKS = 1;"))
        .subscribe(testContext::completeNow);
  }

  private static void deployMainVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
        new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  private void configWebClient(Vertx vertx) {
    webClient = WebClient.create(vertx);
  }

  public static JWTAuth jwtAuth(Vertx vertx, JsonObject config) {
    JsonObject jwtConfig = config.getJsonObject(Constants.JWT_CONFIG_KEY);
    return AuthProviderConfig.jwtProvider(
        vertx,
        jwtConfig.getString(Constants.JWT_CONFIG_ALGORITHM_KEY),
        jwtConfig.getString(Constants.JWT_CONFIG_SECRET_KEY));
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
    user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
    Statement<JsonArray> createUserStatement = userStatements.create(user);
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxUpdateWithParams(
                    createUserStatement.sql(), createUserStatement.params()))
        .map(
            updateResult -> {
              user.setId(updateResult.getKeys().getLong(0));
              user.setToken(
                  jwtAuth.generateToken(
                      new JsonObject()
                          .put("sub", user.getId())
                          .put("complementary-subscription", UUID.randomUUID().toString())));
              return user;
            })
        .flatMap(this::updateUser)
        .map(User::getId)
        .flatMap(this::findUserById);
  }

  protected Single<User> updateUser(User user) {
    Statement<JsonArray> updateUserStatement = userStatements.update(user);
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxUpdateWithParams(
                    updateUserStatement.sql(), updateUserStatement.params()))
        .map(updateResult -> user);
  }

  protected Single<User> findUserById(Long id) {
    Statement<JsonArray> findUserByIdStatement = userStatements.findById(id);
    return SQLClientHelper.inTransactionSingle(
            jdbcClient,
            sqlConnection ->
                sqlConnection.rxQueryWithParams(
                    findUserByIdStatement.sql(), findUserByIdStatement.params()))
        .map(ParserUtils::toUser)
        .map(Optional::get);
  }
}
