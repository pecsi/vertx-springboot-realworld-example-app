package com.example.realworld.infrastructure.web.route;

import com.example.realworld.infrastructure.vertx.proxy.TagsOperations;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

@Component
public class TagsRoute extends AbstractHttpRoute {

  private TagsOperations tagsOperations;

  public TagsRoute(TagsOperations tagsOperations) {
    this.tagsOperations = tagsOperations;
  }

  @Override
  public Router configure(Vertx vertx) {

    String tagsPath = "/tags";

    Router tagsRouter = Router.router(vertx);
    tagsRouter.route().handler(BodyHandler.create());

    tagsRouter.get(tagsPath).handler(this::findTags);

    return tagsRouter;
  }

  private void findTags(RoutingContext routingContext) {
    tagsOperations.findTags(responseOrFail(routingContext, HttpResponseStatus.OK.code(), false));
  }
}
