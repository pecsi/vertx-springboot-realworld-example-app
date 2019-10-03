package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.service.impl.UsersServiceImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    UsersService usersService = new UsersServiceImpl(vertx);

    new ServiceBinder(vertx)
        .setAddress(UsersService.SERVICE_ADDRESS)
        .register(UsersService.class, usersService);

    getConfig()
        .setHandler(
            ar -> {
              DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(ar.result());
              deployVerticle(new UsersAPIVerticle(usersService), deploymentOptions)
                  .setHandler(
                      deployVerticleAsyncResult -> {
                        if (deployVerticleAsyncResult.succeeded()) {
                          startPromise.complete();
                        } else {
                          startPromise.fail(deployVerticleAsyncResult.cause());
                        }
                      });
            });
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
