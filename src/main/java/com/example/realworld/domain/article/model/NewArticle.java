package com.example.realworld.domain.article.model;

import com.example.realworld.application.constants.ValidationMessages;
import com.example.realworld.domain.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class NewArticle {

  @NotBlank(message = ValidationMessages.TITLE_MUST_BE_NOT_BLANK)
  private String title;

  @NotBlank(message = ValidationMessages.DESCRIPTION_MUST_BE_NOT_BLANK)
  private String description;

  @NotBlank(message = ValidationMessages.BODY_MUST_BE_NOT_BLANK)
  private String body;

  @NotNull(message = ValidationMessages.AUTHOR_MUST_BE_NOT_NULL)
  private User author;

  private List<String> tagList;

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

  public List<String> getTagList() {
    return tagList;
  }

  public void setTagList(List<String> tagList) {
    this.tagList = tagList;
  }

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }
}
