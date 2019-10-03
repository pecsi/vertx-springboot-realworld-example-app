package com.example.realworld.infrastructure.verticles;

import com.example.realworld.domain.service.UsersService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class UsersAPIVerticle extends AbstractVerticle {

  private UsersService usersService;

  public UsersAPIVerticle(UsersService usersService) {
    this.usersService = usersService;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();
  }
}
