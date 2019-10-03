package com.example.realworld.domain.service;

import com.example.realworld.domain.service.impl.UsersServiceImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceProxyBuilder;

@ProxyGen
@VertxGen
public interface UsersService {

  String SERVICE_ADDRESS = "users-event-bus-service";
  String SERVICE_NAME = "service.users";

  static UsersService create(Vertx vertx) {
    return new UsersServiceImpl(vertx);
  }

  static UsersService createProxy(Vertx vertx, String address) {
    return new ServiceProxyBuilder(vertx).setAddress(address).build(UsersService.class);
  }

  //  User create(String username, String email, String password);
  //
  //  User login(String email, String password);
  //
  //  User findById(Long id);
  //
  //  User update(User user);
  //
  //  User findByUsername(String username);
}
