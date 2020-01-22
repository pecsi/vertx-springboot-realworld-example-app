package com.example.realworld.domain.article.model;

import com.example.realworld.infrastructure.web.validation.constraint.AtLeastOneFieldMustBeNotNull;

@AtLeastOneFieldMustBeNotNull
public class UpdateArticle {

  private String title;

  private String description;

  private String body;

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
}
