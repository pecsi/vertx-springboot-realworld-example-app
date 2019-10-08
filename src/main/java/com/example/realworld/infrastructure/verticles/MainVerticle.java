package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.service.impl.UsersServiceImpl;
import com.example.realworld.domain.statement.UserStatements;
import com.example.realworld.domain.statement.impl.UserStatementsImpl;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.web.config.AuthProviderConfig;
import com.example.realworld.infrastructure.web.config.ObjectMapperConfig;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLConnection;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
              if (configAR.succeeded()) {

                JsonObject config = configAR.result();

                JDBCClient sqlClient =
                    JDBCClient.createShared(
                        vertx, config.getJsonObject(Constants.DATA_BASE_CONFIG_KEY));

                createApplicationSchema(sqlClient, config)
                    .setHandler(
                        createApplicationSchemaAR -> {
                          if (createApplicationSchemaAR.succeeded()) {

                            JWTAuth jwtProvider = getJwtAuth(config);

                            registerServices(sqlClient, jwtProvider);

                            DeploymentOptions deploymentOptions =
                                new DeploymentOptions().setConfig(config);
                            deployVerticle(
                                    new UsersAPIVerticle(
                                        getProxy(UsersService.class),
                                        ObjectMapperConfig.wrapUnwrapRootValueObjectMapper(),
                                        validator()),
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

              } else {

                startPromise.fail(configAR.cause());
              }
            });
  }

  private JWTAuth getJwtAuth(JsonObject config) {
    JsonObject jwtConfig = config.getJsonObject(Constants.JWT_CONFIG_KEY);

    return AuthProviderConfig.jwtProvider(
        vertx,
        jwtConfig.getString(Constants.JWT_CONFIG_ALGORITHM_KEY),
        jwtConfig.getString(Constants.JWT_CONFIG_SECRET_KEY));
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

  private void registerServices(JDBCClient jdbcClient, JWTAuth jwtProvider) {

    UserStatements userStatements = new UserStatementsImpl();

    register(
        UsersService.class,
        UsersService.SERVICE_ADDRESS,
        new UsersServiceImpl(userStatements, jwtProvider, jdbcClient));
  }

  private <T> T register(Class<T> clazz, String address, T instance) {
    new ServiceBinder(vertx.getDelegate()).setAddress(address).register(clazz, instance);
    T proxy = createProxy(clazz, address);
    this.proxyMap.put(clazz, proxy);
    return proxy;
  }

  private <T> T createProxy(Class<T> clazz, String address) {
    return new ServiceProxyBuilder(vertx.getDelegate()).setAddress(address).build(clazz);
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

  private Validator validator() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    return validatorFactory.getValidator();
  }
}
