package com.example.realworld.application.data;

import com.example.realworld.domain.article.model.Article;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleData {

  private String slug;
  private String title;
  private String description;
  private String body;
  private List<String> tagList;
  private boolean favorited;
  private long favoritesCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private ProfileData author;

  public ArticleData(Article article) {
    this.slug = article.getSlug();
    this.title = article.getTitle();
    this.description = article.getDescription();
    this.body = article.getBody();
    this.createdAt = article.getCreatedAt();
    this.updatedAt = article.getUpdatedAt();
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

  public boolean isFavorited() {
    return favorited;
  }

  public void setFavorited(boolean favorited) {
    this.favorited = favorited;
  }

  public long getFavoritesCount() {
    return favoritesCount;
  }

  public void setFavoritesCount(long favoritesCount) {
    this.favoritesCount = favoritesCount;
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

  public ProfileData getAuthor() {
    return author;
  }

  public void setAuthor(ProfileData author) {
    this.author = author;
  }
}
