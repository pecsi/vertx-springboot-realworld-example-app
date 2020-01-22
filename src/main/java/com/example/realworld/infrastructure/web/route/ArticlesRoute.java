package com.example.realworld.infrastructure.web.route;

import com.example.realworld.infrastructure.vertx.proxy.ArticleOperations;
import com.example.realworld.infrastructure.web.model.request.NewArticleRequest;
import com.example.realworld.infrastructure.web.model.request.UpdateArticleRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticlesRoute extends AbstractHttpRoute {

  private static final String ARTICLES_PATH = "/articles";
  private static final String FEED_PATH = ARTICLES_PATH + "/feed";
  private static final String SLUG_PARAM = "slug";
  private static final String SLUG_PATH = ARTICLES_PATH + "/:" + SLUG_PARAM;
  public static final String OFFSET = "offset";
  public static final String LIMIT = "limit";
  private ArticleOperations articleOperations;

  public ArticlesRoute(ArticleOperations articleOperations) {
    this.articleOperations = articleOperations;
  }

  @Override
  public Router configure(Vertx vertx) {

    String articlesPath = "/articles";

    final Router articlesRouter = Router.router(vertx);

    articlesRouter.route().handler(BodyHandler.create());

    articlesRouter
        .route(articlesPath + "/*")
        .handler(routingContext -> jwtHandler(routingContext, true));

    articlesRouter.get(FEED_PATH).handler(this::feed);
    articlesRouter.get(ARTICLES_PATH).handler(this::getArticles);
    articlesRouter.post(ARTICLES_PATH).handler(this::create);
    articlesRouter.get(SLUG_PATH).handler(this::findBySlug);
    articlesRouter.put(SLUG_PATH).handler(this::updateBySlug);
    articlesRouter.delete(SLUG_PATH).handler(this::deleteBySlug);

    return articlesRouter;
  }

  private void deleteBySlug(RoutingContext routingContext) {
    userId(
        routingContext,
        false,
        (String userId) -> {
          String slug = routingContext.pathParam(SLUG_PARAM);
          articleOperations.deleteBySlug(
              slug, userId, responseOrFail(routingContext, HttpResponseStatus.OK.code(), false));
        });
  }

  private void updateBySlug(RoutingContext routingContext) {
    userId(
        routingContext,
        false,
        (String userId) -> {
          String slug = routingContext.pathParam(SLUG_PARAM);
          UpdateArticleRequest updateArticleRequest =
              getBody(routingContext, UpdateArticleRequest.class);
          articleOperations.updateBySlug(
              slug,
              userId,
              updateArticleRequest,
              responseOrFail(routingContext, HttpResponseStatus.OK.code(), true));
        });
  }

  private void findBySlug(RoutingContext routingContext) {
    userId(
        routingContext,
        true,
        (String userId) -> {
          String slug = routingContext.pathParam(SLUG_PARAM);
          articleOperations.findBySlug(
              slug, userId, responseOrFail(routingContext, HttpResponseStatus.OK.code(), true));
        });
  }

  private void create(RoutingContext routingContext) {
    userId(
        routingContext,
        false,
        (String userId) -> {
          NewArticleRequest newArticleRequest = getBody(routingContext, NewArticleRequest.class);
          articleOperations.create(
              userId,
              newArticleRequest,
              responseOrFail(routingContext, HttpResponseStatus.CREATED.code(), true));
        });
  }

  private void getArticles(RoutingContext routingContext) {
    userId(
        routingContext,
        true,
        (String userId) -> {
          MultiMap queryParams = routingContext.queryParams();
          int offset = getQueryParam(queryParams, OFFSET, 0);
          int limit = getQueryParam(queryParams, LIMIT, 20);
          List<String> tags = queryParams.getAll("tag");
          List<String> authors = queryParams.getAll("author");
          List<String> favorited = queryParams.getAll("favorited");
          articleOperations.findArticles(
              userId,
              offset,
              limit,
              tags,
              authors,
              favorited,
              responseOrFail(routingContext, HttpResponseStatus.OK.code(), false));
        });
  }

  private void feed(RoutingContext routingContext) {
    userId(
        routingContext,
        false,
        (String userId) -> {
          MultiMap queryParams = routingContext.queryParams();
          int offset = getQueryParam(queryParams, OFFSET, 0);
          int limit = getQueryParam(queryParams, LIMIT, 20);
          articleOperations.findRecentArticles(
              userId,
              offset,
              limit,
              responseOrFail(routingContext, HttpResponseStatus.OK.code(), false));
        });
  }

  private int getQueryParam(MultiMap queryParams, String name, int defaultValue) {
    String queryParam = queryParams.get(name);
    return queryParam != null ? Integer.parseInt(queryParam) : defaultValue;
  }
}
