package com.example.realworld.infrastructure.verticles;

import com.example.realworld.infrastructure.web.route.HttpRoute;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.Promise;

import java.util.List;

@Singleton
public class HttpVerticle extends AbstractHttpVerticle {

  private List<HttpRoute> httpRoutes;

  @Inject
  public HttpVerticle(List<HttpRoute> httpRoutes) {
    this.httpRoutes = httpRoutes;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    createHttpServer(this.httpRoutes, startPromise);
  }
}
