package com.example.realworld.infrastructure.verticles;

import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.config.ObjectMapperConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class AbstractVerticleTest {

  protected WebClient webClient;
  protected static JsonObject config;
  protected static SQLClient sqlClient;
  protected static ObjectMapper objectMapper = ObjectMapperConfig.wrapUnwrapRootValueObjectMapper();
  protected static int port;

  @BeforeAll
  public static void beforeAll(Vertx vertx, VertxTestContext testContext) {

    getConfig(vertx)
        .setHandler(
            getConfigAsyncResult -> {
              if (getConfigAsyncResult.succeeded()) {
                config = getConfigAsyncResult.result();
                port = config.getInteger(Constants.SERVER_PORT_KEY);
                sqlClient =
                    JDBCClient.createNonShared(vertx, config.getJsonObject("database_config"));
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
}
