package com.example.realworld.infrastructure.verticles;

import com.example.realworld.infrastructure.context.ApplicationContext;
import com.example.realworld.infrastructure.context.impl.ApplicationContextImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLConnection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class MainVerticle extends AbstractVerticle {

  private ApplicationContext applicationContext;

  public MainVerticle() {
    super();
    this.applicationContext = new ApplicationContextImpl();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    getConfig()
        .setHandler(
            configAR -> {
              if (configAR.succeeded()) {

                JsonObject config = configAR.result();

                applicationContext.instantiateServices(vertx, config);

                JDBCClient jdbcClient = applicationContext.getInstance(JDBCClient.class);

                createApplicationSchema(jdbcClient, config)
                    .setHandler(
                        createApplicationSchemaAR -> {
                          if (createApplicationSchemaAR.succeeded()) {

                            UsersAPIVerticle usersAPIVerticle =
                                applicationContext.getInstance(UsersAPIVerticle.class);

                            DeploymentOptions deploymentOptions =
                                new DeploymentOptions().setConfig(config);
                            deployVerticle(usersAPIVerticle, deploymentOptions)
                                .setHandler(
                                    deployVerticleAsyncResult -> {
                                      if (deployVerticleAsyncResult.succeeded()) {
                                        startPromise.complete();
                                      } else {
                                        startPromise.fail(deployVerticleAsyncResult.cause());
                                      }
                                    });
                          } else {
                            startPromise.fail(createApplicationSchemaAR.cause());
                          }
                        });

              } else {

                startPromise.fail(configAR.cause());
              }
            });
  }

  private Future<Void> createApplicationSchema(JDBCClient jdbcClient, JsonObject config) {

    String finalSchema = readSchema(config);

    return Future.future(
        createApplicationSchemaPromise -> {
          jdbcClient.getConnection(
              sqlConnectionAsyncResult -> {
                if (sqlConnectionAsyncResult.succeeded()) {

                  SQLConnection sqlConnection = sqlConnectionAsyncResult.result();

                  sqlConnection.execute(
                      finalSchema,
                      queryAsyncResult -> {
                        if (queryAsyncResult.succeeded()) {
                          createApplicationSchemaPromise.complete();
                        } else {
                          createApplicationSchemaPromise.fail(queryAsyncResult.cause());
                        }
                      });

                } else {
                  createApplicationSchemaPromise.fail(sqlConnectionAsyncResult.cause());
                }
              });
        });
  }

  private String readSchema(JsonObject config) {
    String schema = "";

    try {
      Path schemaPath =
          Paths.get(
              Objects.requireNonNull(
                      getClass()
                          .getClassLoader()
                          .getResource(config.getString("database_schema_file")))
                  .toURI());

      schema = new String(Files.readAllBytes(schemaPath), StandardCharsets.UTF_8);

    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException(e);
    }

    return schema.replaceAll("\n", "");
  }

  private Future<Void> deployVerticle(Verticle verticle, DeploymentOptions deploymentOptions) {
    return Future.future(
        promise ->
            vertx.deployVerticle(
                verticle,
                deploymentOptions,
                ar -> {
                  if (ar.succeeded()) {
                    promise.complete();
                  } else {
                    promise.fail(ar.cause());
                  }
                }));
  }

  private Future<JsonObject> getConfig() {
    return Future.future(
        jsonObjectPromise -> {
          ConfigStoreOptions fileStore =
              new ConfigStoreOptions()
                  .setType("file")
                  .setConfig(new JsonObject().put("path", "conf/config.json"));
          ConfigRetrieverOptions configRetrieverOptions =
              new ConfigRetrieverOptions().addStore(fileStore);
          ConfigRetriever.create(vertx.getDelegate(), configRetrieverOptions)
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
}
