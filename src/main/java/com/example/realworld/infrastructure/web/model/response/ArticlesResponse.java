package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.domain.article.model.Article;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

@DataObject(generateConverter = true)
public class ArticlesResponse {

  private List<ArticleResponse> articles;
  private Long articlesCount;

  public ArticlesResponse() {}

  public ArticlesResponse(JsonObject jsonObject) {
    ArticlesResponseConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    ArticlesResponseConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public ArticlesResponse(List<Article> result, Long articlesCount) {
    this.articles = result.stream().map(ArticleResponse::new).collect(Collectors.toList());
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
