package com.example.realworld.infrastructure.configuration;

import com.example.realworld.application.UserServiceImpl;
import com.example.realworld.domain.user.CryptographyService;
import com.example.realworld.domain.user.model.TokenProvider;
import com.example.realworld.domain.user.model.UserRepository;
import com.example.realworld.domain.user.service.UserService;
import com.example.realworld.infrastructure.vertx.configuration.VertxConfiguration;
import com.example.realworld.infrastructure.vertx.proxy.UserOperations;
import com.example.realworld.infrastructure.vertx.proxy.impl.UserOperationsImpl;
import com.example.realworld.infrastructure.web.config.AuthProviderConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RealworldApplicationConfiguration {

  @Bean
  public UserService userService(
      UserRepository userRepository,
      CryptographyService cryptographyService,
      TokenProvider tokenProvider) {
    return new UserServiceImpl(userRepository, cryptographyService, tokenProvider);
  }

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

  @Bean
  public JWTAuth jwtAuth(Vertx vertx, VertxConfiguration vertxConfiguration) {
    VertxConfiguration.Jwt jwt = vertxConfiguration.getJwt();
    return AuthProviderConfig.jwtProvider(vertx, jwt.getAlgorithm(), jwt.getSecret());
  }

  @Bean("wrapUnwrapRootValueObjectMapper")
  public ObjectMapper wrapUnwrapRootValueObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
    objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean("defaultObjectMapper")
  public ObjectMapper defaultObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean
  public UserOperations userOperations(
      Vertx vertx,
      UserService userService,
      @Qualifier("defaultObjectMapper") ObjectMapper objectMapper) {
    return registerServiceAndCreateProxy(
        vertx,
        UserOperations.class,
        UserOperations.SERVICE_ADDRESS,
        new UserOperationsImpl(userService, objectMapper));
  }

  private <T> T registerServiceAndCreateProxy(
      Vertx vertx, Class<T> clazz, String address, T instance) {
    registerProxy(vertx, clazz, address, instance);
    return createProxy(vertx, clazz, address);
  }

  private <T> void registerProxy(Vertx vertx, Class<T> clazz, String address, T instance) {
    new ServiceBinder(vertx.getDelegate()).setAddress(address).register(clazz, instance);
  }

  private <T> T createProxy(Vertx vertx, Class<T> clazz, String address) {
    return new ServiceProxyBuilder(vertx.getDelegate()).setAddress(address).build(clazz);
  }
}
