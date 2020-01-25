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

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.Vertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;
import io.vertx.serviceproxy.ProxyUtils;

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
public class ArticleOperationsVertxEBProxy implements ArticleOperations {
  private Vertx _vertx;
  private String _address;
  private DeliveryOptions _options;
  private boolean closed;

  public ArticleOperationsVertxEBProxy(Vertx vertx, String address) {
    this(vertx, address, null);
  }

  public ArticleOperationsVertxEBProxy(Vertx vertx, String address, DeliveryOptions options) {
    this._vertx = vertx;
    this._address = address;
    this._options = options;
    try{
      this._vertx.eventBus().registerDefaultCodec(ServiceException.class, new ServiceExceptionMessageCodec());
    } catch (IllegalStateException ex) {}
  }

  @Override
  public  void findRecentArticles(String currentUserId, int offset, int limit, Handler<AsyncResult<ArticlesResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("currentUserId", currentUserId);
    _json.put("offset", offset);
    _json.put("limit", limit);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "findRecentArticles");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new ArticlesResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void findArticles(String currentUserId, int offset, int limit, List<String> tags, List<String> authors, List<String> favorited, Handler<AsyncResult<ArticlesResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("currentUserId", currentUserId);
    _json.put("offset", offset);
    _json.put("limit", limit);
    _json.put("tags", new JsonArray(tags));
    _json.put("authors", new JsonArray(authors));
    _json.put("favorited", new JsonArray(favorited));

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "findArticles");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new ArticlesResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void create(String currentUserId, NewArticleRequest newArticleRequest, Handler<AsyncResult<ArticleResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("currentUserId", currentUserId);
    _json.put("newArticleRequest", newArticleRequest == null ? null : newArticleRequest.toJson());

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "create");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new ArticleResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void findBySlug(String slug, String currentUserId, Handler<AsyncResult<ArticleResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("slug", slug);
    _json.put("currentUserId", currentUserId);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "findBySlug");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new ArticleResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void updateBySlug(String slug, String currentUserId, UpdateArticleRequest updateArticleRequest, Handler<AsyncResult<ArticleResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("slug", slug);
    _json.put("currentUserId", currentUserId);
    _json.put("updateArticleRequest", updateArticleRequest == null ? null : updateArticleRequest.toJson());

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "updateBySlug");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new ArticleResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void deleteArticleBySlug(String slug, String currentUserId, Handler<AsyncResult<Void>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("slug", slug);
    _json.put("currentUserId", currentUserId);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "deleteArticleBySlug");
    _vertx.eventBus().<Void>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body()));
      }
    });
  }
  @Override
  public  void createCommentBySlug(String slug, String currentUserId, NewCommentRequest newCommentRequest, Handler<AsyncResult<CommentResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("slug", slug);
    _json.put("currentUserId", currentUserId);
    _json.put("newCommentRequest", newCommentRequest == null ? null : newCommentRequest.toJson());

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "createCommentBySlug");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new CommentResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void deleteCommentByIdAndAuthorId(String commentId, String currentUserId, Handler<AsyncResult<Void>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("commentId", commentId);
    _json.put("currentUserId", currentUserId);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "deleteCommentByIdAndAuthorId");
    _vertx.eventBus().<Void>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body()));
      }
    });
  }
  @Override
  public  void findCommentsBySlug(String slug, String currentUserId, Handler<AsyncResult<CommentsResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("slug", slug);
    _json.put("currentUserId", currentUserId);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "findCommentsBySlug");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new CommentsResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void favoriteArticle(String slug, String currentUserId, Handler<AsyncResult<ArticleResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("slug", slug);
    _json.put("currentUserId", currentUserId);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "favoriteArticle");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new ArticleResponse(res.result().body())));
      }
    });
  }
  @Override
  public  void unfavoriteArticle(String slug, String currentUserId, Handler<AsyncResult<ArticleResponse>> handler){
    if (closed) {
      handler.handle(Future.failedFuture(new IllegalStateException("Proxy is closed")));
      return;
    }
    JsonObject _json = new JsonObject();
    _json.put("slug", slug);
    _json.put("currentUserId", currentUserId);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "unfavoriteArticle");
    _vertx.eventBus().<JsonObject>send(_address, _json, _deliveryOptions, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().body() == null ? null : new ArticleResponse(res.result().body())));
      }
    });
  }
}
