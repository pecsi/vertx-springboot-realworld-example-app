package com.example.realworld.infrastructure.web.route;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;

public interface HttpRoute {

  Router configure(Vertx vertx);
}
