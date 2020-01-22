package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.domain.article.model.UpdateArticle;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@JsonRootName("article")
@DataObject(generateConverter = true)
public class UpdateArticleRequest {

  private String title;

  private String description;

  private String body;

  public UpdateArticleRequest() {}

  public UpdateArticleRequest(JsonObject jsonObject) {
    UpdateArticleRequestConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    UpdateArticleRequestConverter.toJson(this, jsonObject);
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

  public UpdateArticle toUpdateArticle() {
    UpdateArticle updateArticle = new UpdateArticle();
    updateArticle.setTitle(this.title);
    updateArticle.setDescription(this.description);
    updateArticle.setBody(this.body);
    return updateArticle;
  }
}
