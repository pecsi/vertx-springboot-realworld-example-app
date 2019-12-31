package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.profile.model.Profile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;
import java.util.List;

@JsonRootName("article")
@DataObject(generateConverter = true)
public class ArticleResponse {

  private String slug;
  private String title;
  private String description;
  private String body;
  private List<String> tagList;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime updatedAt;

  private boolean favorited;
  private int favoritesCount;
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

  public ArticleResponse(Article article, Profile profile, List<String> tagList) {
    this.slug = article.getSlug();
    this.title = article.getTitle();
    this.description = article.getDescription();
    this.body = article.getBody();
    this.createdAt = article.getCreatedAt();
    this.updatedAt = article.getUpdatedAt();
    this.tagList = tagList;
    //    this.favorited = article.isFavorited();
    //    this.favoritesCount = article.getFavoritesCount();
    this.author = new ProfileResponse(profile);
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public boolean isFavorited() {
    return favorited;
  }

  public void setFavorited(boolean favorited) {
    this.favorited = favorited;
  }

  public int getFavoritesCount() {
    return favoritesCount;
  }

  public void setFavoritesCount(int favoritesCount) {
    this.favoritesCount = favoritesCount;
  }

  public ProfileResponse getAuthor() {
    return author;
  }

  public void setAuthor(ProfileResponse author) {
    this.author = author;
  }
}
