package com.example.realworld.infrastructure.context.impl;

import com.example.realworld.domain.service.UsersService;
import com.example.realworld.domain.service.impl.UsersServiceImpl;
import com.example.realworld.domain.statement.UserStatements;
import com.example.realworld.domain.statement.impl.UserStatementsImpl;
import com.example.realworld.infrastructure.Constants;
import com.example.realworld.infrastructure.context.ApplicationContext;
import com.example.realworld.infrastructure.verticles.UsersAPIVerticle;
import com.example.realworld.infrastructure.web.config.AuthProviderConfig;
import com.example.realworld.infrastructure.web.config.ObjectMapperConfig;
import com.example.realworld.infrastructure.web.exception.mapper.BusinessExceptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContextImpl implements ApplicationContext {

  private Map<Class<?>, Object> contextMap = new HashMap<>();

  @Override
  public void instantiateServices(Vertx vertx, JsonObject config) {

    ObjectMapper wrapUnwrapRootValueObjectMapper =
        ObjectMapperConfig.wrapUnwrapRootValueObjectMapper();

    addToContext(ObjectMapper.class, wrapUnwrapRootValueObjectMapper);

    JDBCClient jdbcClient =
        JDBCClient.createShared(vertx, config.getJsonObject(Constants.DATA_BASE_CONFIG_KEY));

    addToContext(JDBCClient.class, jdbcClient);

    JWTAuth jwtProvider = getJwtAuth(vertx, config);

    addToContext(JWTAuth.class, jwtProvider);

    UserStatements userStatements = new UserStatementsImpl();

    addToContext(UserStatements.class, userStatements);

    ObjectMapper defaultObjectMapper = ObjectMapperConfig.defaultObjectMapper();

    UsersService usersService =
        registerProxy(
            vertx,
            UsersService.class,
            UsersService.SERVICE_ADDRESS,
            new UsersServiceImpl(userStatements, jwtProvider, jdbcClient, defaultObjectMapper));

    addToContext(UsersService.class, usersService);

    Validator validator = getValidator();

    addToContext(Validator.class, validator);

    BusinessExceptionMapper businessExceptionMapper =
        new BusinessExceptionMapper(wrapUnwrapRootValueObjectMapper, defaultObjectMapper);

    addToContext(BusinessExceptionMapper.class, businessExceptionMapper);

    UsersAPIVerticle usersAPIVerticle =
        new UsersAPIVerticle(
            usersService,
            wrapUnwrapRootValueObjectMapper,
            defaultObjectMapper,
            validator,
            businessExceptionMapper);

    addToContext(UsersAPIVerticle.class, usersAPIVerticle);
  }

  private <T> void addToContext(Class<T> clazz, T instance) {
    this.contextMap.put(clazz, instance);
  }

  private JWTAuth getJwtAuth(Vertx vertx, JsonObject config) {
    JsonObject jwtConfig = config.getJsonObject(Constants.JWT_CONFIG_KEY);
    return AuthProviderConfig.jwtProvider(
        vertx,
        jwtConfig.getString(Constants.JWT_CONFIG_ALGORITHM_KEY),
        jwtConfig.getString(Constants.JWT_CONFIG_SECRET_KEY));
  }

  private Validator getValidator() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    return validatorFactory.getValidator();
  }

  private <T> T registerProxy(Vertx vertx, Class<T> clazz, String address, T instance) {
    new ServiceBinder(vertx.getDelegate()).setAddress(address).register(clazz, instance);
    T proxy = createProxy(vertx, clazz, address);
    this.contextMap.put(clazz, proxy);
    return proxy;
  }

  private <T> T createProxy(Vertx vertx, Class<T> clazz, String address) {
    return new ServiceProxyBuilder(vertx.getDelegate()).setAddress(address).build(clazz);
  }

  @Override
  public <T> T getInstance(Class<T> clazz) {
    return clazz.cast(this.contextMap.get(clazz));
  }
}
