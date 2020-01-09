package com.example.realworld.infrastructure.web.model.response;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

@DataObject(generateConverter = true)
public class ArticlesFeedResponse {

  private List<ArticleResponse> articles;
  private Long articlesCount;

  public ArticlesFeedResponse() {}

  public ArticlesFeedResponse(JsonObject jsonObject) {
    ArticlesFeedResponseConverter.fromJson(jsonObject, this);
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

  public Long getArticlesCount() {
    return articlesCount;
  }

  public void setArticlesCount(Long articlesCount) {
    this.articlesCount = articlesCount;
  }
}
