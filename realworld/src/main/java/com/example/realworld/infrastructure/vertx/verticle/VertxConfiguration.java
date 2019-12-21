package com.example.realworld.infrastructure.vertx.verticle;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("vertx")
public class VertxConfiguration {

  private Server server;
  private Jwt jwt;
  private Database database;

  @Getter
  @Setter
  static class Server {
    private int port;
    private String contextPath;
  }

  @Getter
  @Setter
  static class Jwt {
    private String algorithm;
    private String secret;
  }

  @Getter
  @Setter
  public static class Database {
    @Value("${spring.datasource.url}")
    private String url;

    private String driverClass;
    private int maxPoolSize;
    private String user;
    private String password;
  }
}
