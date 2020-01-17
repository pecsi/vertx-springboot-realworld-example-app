package com.example.realworld.domain.article.model;

import com.example.realworld.application.constants.ValidationMessages;
import com.example.realworld.domain.tag.model.NewTag;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class NewArticle {

  @NotBlank(message = ValidationMessages.TITLE_MUST_BE_NOT_BLANK)
  private String title;

  @NotBlank(message = ValidationMessages.DESCRIPTION_MUST_BE_NOT_BLANK)
  private String description;

  @NotBlank(message = ValidationMessages.BODY_MUST_BE_NOT_BLANK)
  private String body;

  private List<NewTag> tags;

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

  public List<NewTag> getTags() {
    return tags;
  }

  public void setTags(List<NewTag> tags) {
    this.tags = tags;
  }
}
