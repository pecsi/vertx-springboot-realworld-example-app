package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.service.impl.UsersServiceImpl;
import com.example.realworld.repository.UserRepository;
import com.example.realworld.repository.impl.UserRepositoryImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainVerticle extends AbstractVerticle {

  private Map<Class<?>, Object> proxyMap = new HashMap<>();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    getConfig()
        .setHandler(
            configAR -> {
              JsonObject config = configAR.result();

              SQLClient sqlClient =
                  JDBCClient.createShared(vertx, config.getJsonObject("database_config"));

              createApplicationSchema(sqlClient, config)
                  .setHandler(
                      createApplicationSchemaAR -> {
                        if (createApplicationSchemaAR.succeeded()) {
                          registerServices(sqlClient);

                          DeploymentOptions deploymentOptions =
                              new DeploymentOptions().setConfig(config);
                          deployVerticle(
                                  new UsersAPIVerticle(getProxy(UsersService.class)),
                                  deploymentOptions)
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
            });
  }

  private Future<Void> createApplicationSchema(SQLClient sqlClient, JsonObject config) {

    String finalSchema = readSchema(config);

    return Future.future(
        createApplicationSchemaPromise -> {
          sqlClient.getConnection(
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

  private void registerServices(SQLClient sqlClient) {

    UserRepository userRepositoryProxy =
        register(
            UserRepository.class,
            UserRepository.SERVICE_ADDRESS,
            new UserRepositoryImpl(sqlClient));

    register(
        UsersService.class,
        UsersService.SERVICE_ADDRESS,
        new UsersServiceImpl(userRepositoryProxy));
  }

  private <T> T register(Class<T> clazz, String address, T instance) {
    new ServiceBinder(vertx).setAddress(address).register(clazz, instance);
    T proxy = createProxy(clazz, address);
    this.proxyMap.put(clazz, proxy);
    return proxy;
  }

  private <T> T createProxy(Class<T> clazz, String address) {
    return new ServiceProxyBuilder(vertx).setAddress(address).build(clazz);
  }

  private <T> T getProxy(Class<T> clazz) {
    return clazz.cast(this.proxyMap.get(clazz));
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
}
