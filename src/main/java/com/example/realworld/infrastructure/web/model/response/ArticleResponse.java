package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.application.data.ArticleData;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

@JsonRootName("article")
@DataObject(generateConverter = true)
public class ArticleResponse {

  private static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private String slug;
  private String title;
  private String description;
  private String body;
  private List<String> tagList;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern)
  private String createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern)
  private String updatedAt;

  private boolean favorited;
  private Long favoritesCount;
  private ProfileResponse author;

  public ArticleResponse() {}

  public ArticleResponse(JsonObject jsonObject) {
    ArticleResponseConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    ArticleResponseConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public ArticleResponse(ArticleData article) {
    this.slug = article.getSlug();
    this.title = article.getTitle();
    this.description = article.getDescription();
    this.body = article.getBody();
    this.createdAt = ParserUtils.format(article.getCreatedAt(), datePattern);
    this.updatedAt = ParserUtils.format(article.getUpdatedAt(), datePattern);
    this.tagList = article.getTagList();
    this.favorited = article.isFavorited();
    this.favoritesCount = article.getFavoritesCount();
    this.author = new ProfileResponse(article.getAuthor());
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
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

  public List<String> getTagList() {
    return tagList;
  }

  public void setTagList(List<String> tagList) {
    this.tagList = tagList;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public boolean isFavorited() {
    return favorited;
  }

  public void setFavorited(boolean favorited) {
    this.favorited = favorited;
  }

  public Long getFavoritesCount() {
    return favoritesCount;
  }

  public void setFavoritesCount(Long favoritesCount) {
    this.favoritesCount = favoritesCount;
  }

  public ProfileResponse getAuthor() {
    return author;
  }

  public void setAuthor(ProfileResponse author) {
    this.author = author;
  }
}
