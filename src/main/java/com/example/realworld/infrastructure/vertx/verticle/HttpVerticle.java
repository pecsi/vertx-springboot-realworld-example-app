package com.example.realworld.infrastructure.vertx.verticle;

import com.example.realworld.infrastructure.web.route.HttpRoute;
import io.vertx.core.Promise;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HttpVerticle extends AbstractHttpVerticle {

  private List<HttpRoute> httpRoutes;

  public HttpVerticle(List<HttpRoute> httpRoutes) {
    this.httpRoutes = httpRoutes;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    createHttpServer(this.httpRoutes, startPromise);
  }
}
