/*
* Copyright 2014 Red Hat, Inc.
*
* Red Hat licenses this file to you under the Apache License, version 2.0
* (the "License"); you may not use this file except in compliance with the
* License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/

package com.example.realworld.infrastructure.vertx.proxy;

import com.example.realworld.infrastructure.vertx.proxy.ArticleOperations;
import io.vertx.core.Vertx;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ProxyHandler;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;
import io.vertx.serviceproxy.HelperUtils;

import java.util.List;
import com.example.realworld.infrastructure.web.model.request.NewArticleRequest;
import com.example.realworld.infrastructure.web.model.response.ArticleResponse;
import com.example.realworld.infrastructure.web.model.request.NewCommentRequest;
import com.example.realworld.infrastructure.web.model.response.ArticlesResponse;
import com.example.realworld.infrastructure.web.model.response.CommentsResponse;
import com.example.realworld.infrastructure.web.model.request.UpdateArticleRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import com.example.realworld.infrastructure.web.model.response.CommentResponse;
/*
  Generated Proxy code - DO NOT EDIT
  @author Roger the Robot
*/

@SuppressWarnings({"unchecked", "rawtypes"})
public class ArticleOperationsVertxProxyHandler extends ProxyHandler {

  public static final long DEFAULT_CONNECTION_TIMEOUT = 5 * 60; // 5 minutes 
  private final Vertx vertx;
  private final ArticleOperations service;
  private final long timerID;
  private long lastAccessed;
  private final long timeoutSeconds;

  public ArticleOperationsVertxProxyHandler(Vertx vertx, ArticleOperations service){
    this(vertx, service, DEFAULT_CONNECTION_TIMEOUT);
  }

  public ArticleOperationsVertxProxyHandler(Vertx vertx, ArticleOperations service, long timeoutInSecond){
    this(vertx, service, true, timeoutInSecond);
  }

  public ArticleOperationsVertxProxyHandler(Vertx vertx, ArticleOperations service, boolean topLevel, long timeoutSeconds) {
      this.vertx = vertx;
      this.service = service;
      this.timeoutSeconds = timeoutSeconds;
      try {
        this.vertx.eventBus().registerDefaultCodec(ServiceException.class,
            new ServiceExceptionMessageCodec());
      } catch (IllegalStateException ex) {}
      if (timeoutSeconds != -1 && !topLevel) {
        long period = timeoutSeconds * 1000 / 2;
        if (period > 10000) {
          period = 10000;
        }
        this.timerID = vertx.setPeriodic(period, this::checkTimedOut);
      } else {
        this.timerID = -1;
      }
      accessed();
    }


  private void checkTimedOut(long id) {
    long now = System.nanoTime();
    if (now - lastAccessed > timeoutSeconds * 1000000000) {
      close();
    }
  }

    @Override
    public void close() {
      if (timerID != -1) {
        vertx.cancelTimer(timerID);
      }
      super.close();
    }

    private void accessed() {
      this.lastAccessed = System.nanoTime();
    }

  public void handle(Message<JsonObject> msg) {
    try{
      JsonObject json = msg.body();
      String action = msg.headers().get("action");
      if (action == null) throw new IllegalStateException("action not specified");
      accessed();
      switch (action) {
        case "findRecentArticles": {
          service.findRecentArticles((java.lang.String)json.getValue("currentUserId"),
                        json.getValue("offset") == null ? null : (json.getLong("offset").intValue()),
                        json.getValue("limit") == null ? null : (json.getLong("limit").intValue()),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "findArticles": {
          service.findArticles((java.lang.String)json.getValue("currentUserId"),
                        json.getValue("offset") == null ? null : (json.getLong("offset").intValue()),
                        json.getValue("limit") == null ? null : (json.getLong("limit").intValue()),
                        HelperUtils.convertList(json.getJsonArray("tags").getList()),
                        HelperUtils.convertList(json.getJsonArray("authors").getList()),
                        HelperUtils.convertList(json.getJsonArray("favorited").getList()),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "create": {
          service.create((java.lang.String)json.getValue("currentUserId"),
                        json.getJsonObject("newArticleRequest") == null ? null : new com.example.realworld.infrastructure.web.model.request.NewArticleRequest(json.getJsonObject("newArticleRequest")),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "findBySlug": {
          service.findBySlug((java.lang.String)json.getValue("slug"),
                        (java.lang.String)json.getValue("currentUserId"),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "updateBySlug": {
          service.updateBySlug((java.lang.String)json.getValue("slug"),
                        (java.lang.String)json.getValue("currentUserId"),
                        json.getJsonObject("updateArticleRequest") == null ? null : new com.example.realworld.infrastructure.web.model.request.UpdateArticleRequest(json.getJsonObject("updateArticleRequest")),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "deleteArticleBySlug": {
          service.deleteArticleBySlug((java.lang.String)json.getValue("slug"),
                        (java.lang.String)json.getValue("currentUserId"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "createCommentBySlug": {
          service.createCommentBySlug((java.lang.String)json.getValue("slug"),
                        (java.lang.String)json.getValue("currentUserId"),
                        json.getJsonObject("newCommentRequest") == null ? null : new com.example.realworld.infrastructure.web.model.request.NewCommentRequest(json.getJsonObject("newCommentRequest")),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "deleteCommentByIdAndAuthorId": {
          service.deleteCommentByIdAndAuthorId((java.lang.String)json.getValue("commentId"),
                        (java.lang.String)json.getValue("currentUserId"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "findCommentsBySlug": {
          service.findCommentsBySlug((java.lang.String)json.getValue("slug"),
                        (java.lang.String)json.getValue("currentUserId"),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "favoriteArticle": {
          service.favoriteArticle((java.lang.String)json.getValue("slug"),
                        (java.lang.String)json.getValue("currentUserId"),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        case "unfavoriteArticle": {
          service.unfavoriteArticle((java.lang.String)json.getValue("slug"),
                        (java.lang.String)json.getValue("currentUserId"),
                        res -> {
                        if (res.failed()) {
                          if (res.cause() instanceof ServiceException) {
                            msg.reply(res.cause());
                          } else {
                            msg.reply(new ServiceException(-1, res.cause().getMessage()));
                          }
                        } else {
                          msg.reply(res.result() == null ? null : res.result().toJson());
                        }
                     });
          break;
        }
        default: throw new IllegalStateException("Invalid action: " + action);
      }
    } catch (Throwable t) {
      msg.reply(new ServiceException(500, t.getMessage()));
      throw t;
    }
  }
}