package com.example.realworld.infrastructure.vertx.proxy;

import com.example.realworld.infrastructure.web.model.response.TagsResponse;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
@VertxGen
public interface TagsOperations {

  String SERVICE_ADDRESS = "tags-service-event-bus";

  void findTags(Handler<AsyncResult<TagsResponse>> handler);
}
