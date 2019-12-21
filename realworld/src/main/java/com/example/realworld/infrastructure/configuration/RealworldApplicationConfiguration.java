package com.example.realworld.infrastructure.configuration;

import com.example.realworld.infrastructure.vertx.verticle.VertxConfiguration;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RealworldApplicationConfiguration {

  @Bean
  public Vertx vertx() {
    return Vertx.vertx();
  }

  @Bean
  public JDBCClient jdbcClient(Vertx vertx, VertxConfiguration vertxConfiguration) {
    VertxConfiguration.Database database = vertxConfiguration.getDatabase();
    JsonObject dataBaseConfig = new JsonObject();
    dataBaseConfig.put("url", database.getUrl());
    dataBaseConfig.put("driver_class", database.getDriverClass());
    dataBaseConfig.put("max_pool_size", database.getMaxPoolSize());
    dataBaseConfig.put("user", database.getUser());
    dataBaseConfig.put("password", database.getPassword());
    return JDBCClient.createShared(vertx, dataBaseConfig);
  }
}
