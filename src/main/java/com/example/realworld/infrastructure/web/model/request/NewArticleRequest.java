package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.domain.article.model.NewArticle;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

@JsonRootName("article")
@DataObject(generateConverter = true)
public class NewArticleRequest {

  private String title;

  private String description;

  private String body;

  private List<String> tags;

  public NewArticleRequest() {}

  public NewArticleRequest(JsonObject jsonObject) {
    NewArticleRequestConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    NewArticleRequestConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public NewArticle toNewArticle() {
    NewArticle newArticle = new NewArticle();
    newArticle.setTitle(this.title);
    newArticle.setDescription(this.description);
    newArticle.setBody(this.body);
    newArticle.setTags(this.tags);
    return newArticle;
  }
}
