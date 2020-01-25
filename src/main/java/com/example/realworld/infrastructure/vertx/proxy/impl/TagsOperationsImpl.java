package com.example.realworld.infrastructure.vertx.proxy.impl;

import com.example.realworld.domain.tag.service.TagService;
import com.example.realworld.infrastructure.vertx.proxy.TagsOperations;
import com.example.realworld.infrastructure.web.model.response.TagsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class TagsOperationsImpl extends AbstractOperations implements TagsOperations {

  private TagService tagService;

  public TagsOperationsImpl(TagService tagService, ObjectMapper objectMapper) {
    super(objectMapper);
    this.tagService = tagService;
  }

  @Override
  public void findTags(Handler<AsyncResult<TagsResponse>> handler) {
    tagService
        .findAll()
        .subscribe(
            tags -> handler.handle(Future.succeededFuture(new TagsResponse(tags))),
            throwable -> handler.handle(error(throwable)));
  }
}
