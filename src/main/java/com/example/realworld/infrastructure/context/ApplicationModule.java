package com.example.realworld.infrastructure.context;

import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.service.impl.UsersServiceImpl;
import com.example.realworld.domain.statement.UserStatements;
import com.example.realworld.domain.statement.impl.UserStatementsImpl;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.context.annotation.DefaultObjectMapper;
import com.example.realworld.infrastructure.context.annotation.WrapUnwrapRootValueObjectMapper;
import com.example.realworld.infrastructure.verticles.AbstractAPIVerticle;
import com.example.realworld.infrastructure.verticles.ProfilesAPIVerticle;
import com.example.realworld.infrastructure.verticles.UsersAPIVerticle;
import com.example.realworld.infrastructure.web.config.AuthProviderConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.List;

public class ApplicationModule extends AbstractModule {

  private Vertx vertx;
  private JsonObject config;

  public ApplicationModule(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(JsonObject.class).annotatedWith(Names.named("Config")).toInstance(config);
    bind(UserStatements.class).to(UserStatementsImpl.class).asEagerSingleton();
    bind(UsersAPIVerticle.class);
    bind(ProfilesAPIVerticle.class);
  }

  @Provides
  @Singleton
  public Vertx vertx() {
    return this.vertx;
  }

  @Provides
  @Singleton
  public JDBCClient jdbcClient(@Named("Config") JsonObject config) {
    return JDBCClient.createShared(vertx, config.getJsonObject(Constants.DATA_BASE_CONFIG_KEY));
  }

  @Provides
  @Singleton
  public JWTAuth jwtAuth(Vertx vertx, @Named("Config") JsonObject config) {
    JsonObject jwtConfig = config.getJsonObject(Constants.JWT_CONFIG_KEY);
    return AuthProviderConfig.jwtProvider(
        vertx,
        jwtConfig.getString(Constants.JWT_CONFIG_ALGORITHM_KEY),
        jwtConfig.getString(Constants.JWT_CONFIG_SECRET_KEY));
  }

  @Provides
  @Singleton
  public UsersService usersService(
      UserStatements userStatements,
      JWTAuth jwtAuth,
      JDBCClient jdbcClient,
      @DefaultObjectMapper ObjectMapper objectMapper) {
    return registerServiceAndCreateProxy(
        vertx,
        UsersService.class,
        UsersService.SERVICE_ADDRESS,
        new UsersServiceImpl(userStatements, jwtAuth, jdbcClient, objectMapper));
  }

  @Provides
  @Singleton
  private Validator validator() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    return validatorFactory.getValidator();
  }

  @Provides
  @Singleton
  @WrapUnwrapRootValueObjectMapper
  public ObjectMapper wrapUnwrapRootValueObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
    objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Provides
  @Singleton
  @DefaultObjectMapper
  public ObjectMapper defaultObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Provides
  @Singleton
  public List<AbstractAPIVerticle> apiVerticles(
      UsersAPIVerticle usersAPIVerticle, ProfilesAPIVerticle profilesAPIVerticle) {
    return Arrays.asList(usersAPIVerticle, profilesAPIVerticle);
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
