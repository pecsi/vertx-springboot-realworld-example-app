package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.application.data.ArticlesData;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

@DataObject(generateConverter = true)
public class ArticlesFeedResponse {

  private List<ArticleResponse> articles;
  private long articlesCount;

  public ArticlesFeedResponse() {}

  public ArticlesFeedResponse(JsonObject jsonObject) {
    ArticlesFeedResponseConverter.fromJson(jsonObject, this);
  }

  public ArticlesFeedResponse(ArticlesData articlesData) {
    this.articles =
        articlesData.getArticles().stream().map(ArticleResponse::new).collect(Collectors.toList());
    this.articlesCount = articlesData.getArticlesCount();
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    ArticlesFeedResponseConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public ArticlesFeedResponse(List<ArticleResponse> articles, Long articlesCount) {
    this.articles = articles;
    this.articlesCount = articlesCount;
  }

  public List<ArticleResponse> getArticles() {
    return articles;
  }

  public void setArticles(List<ArticleResponse> articles) {
    this.articles = articles;
  }

  public long getArticlesCount() {
    return articlesCount;
  }

  public void setArticlesCount(long articlesCount) {
    this.articlesCount = articlesCount;
  }
}
