package com.example.realworld.infrastructure.verticles;

import com.example.realworld.infrastructure.context.ApplicationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLConnection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainVerticle extends AbstractVerticle {

  private Injector injector;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    getConfig()
        .setHandler(
            configAR -> {
              if (configAR.succeeded()) {

                JsonObject config = configAR.result();

                injector = Guice.createInjector(new ApplicationModule(vertx, config));

                JDBCClient jdbcClient = injector.getInstance(JDBCClient.class);

                createApplicationSchema(jdbcClient, config)
                    .setHandler(
                        createApplicationSchemaAR -> {
                          if (createApplicationSchemaAR.succeeded()) {

                            List<AbstractAPIVerticle> apiVerticles =
                                injector.getInstance(
                                    Key.get(new TypeLiteral<List<AbstractAPIVerticle>>() {}));

                            DeploymentOptions deploymentOptions =
                                new DeploymentOptions().setConfig(config);

                            CompositeFuture.all(deployVerticles(apiVerticles, deploymentOptions))
                                .setHandler(
                                    compositeFutureAsyncResult -> {
                                      if (compositeFutureAsyncResult.succeeded()) {
                                        startPromise.complete();
                                      } else {
                                        startPromise.fail(compositeFutureAsyncResult.cause());
                                      }
                                    });
                            //                                .subscribe(
                            //                                    compositeFuture ->,
                            // startPromise::fail);

                          } else {
                            startPromise.fail(createApplicationSchemaAR.cause());
                          }
                        });

              } else {

                startPromise.fail(configAR.cause());
              }
            });
  }

  private List<Future> deployVerticles(
      List<AbstractAPIVerticle> abstractAPIVerticles, DeploymentOptions deploymentOptions) {
    List<Future> deployVerticles = new LinkedList<>();
    abstractAPIVerticles.forEach(
        apiVerticle -> deployVerticles.add(deployVerticle(apiVerticle, deploymentOptions)));
    return deployVerticles;
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
