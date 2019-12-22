package com.example.realworld.infrastructure.vertx.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("vertx")
public class VertxConfiguration {

  private Server server;
  private Jwt jwt;
  private Database database;

  public static class Server {
    private int port;
    private String contextPath;

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getContextPath() {
      return contextPath;
    }

    public void setContextPath(String contextPath) {
      this.contextPath = contextPath;
    }
  }

  public static class Jwt {
    private String algorithm;
    private String secret;

    public String getAlgorithm() {
      return algorithm;
    }

    public void setAlgorithm(String algorithm) {
      this.algorithm = algorithm;
    }

    public String getSecret() {
      return secret;
    }

    public void setSecret(String secret) {
      this.secret = secret;
    }
  }

  public static class Database {
    private String url;
    private String driverClass;
    private int maxPoolSize;
    private String user;
    private String password;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getDriverClass() {
      return driverClass;
    }

    public void setDriverClass(String driverClass) {
      this.driverClass = driverClass;
    }

    public int getMaxPoolSize() {
      return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }

    public String getUser() {
      return user;
    }

    public void setUser(String user) {
      this.user = user;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public Server getServer() {
    return server;
  }

  public void setServer(Server server) {
    this.server = server;
  }

  public Jwt getJwt() {
    return jwt;
  }

  public void setJwt(Jwt jwt) {
    this.jwt = jwt;
  }

  public Database getDatabase() {
    return database;
  }

  public void setDatabase(Database database) {
    this.database = database;
  }
}
