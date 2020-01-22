package com.example.realworld.application.data;

import com.example.realworld.domain.article.model.Comment;

import java.time.LocalDateTime;

public class CommentData {

  private String id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String body;
  private ProfileData author;

  public CommentData() {}

  public CommentData(Comment comment, ProfileData author) {
    this.id = comment.getId();
    this.createdAt = comment.getCreatedAt();
    this.updatedAt = comment.getUpdatedAt();
    this.body = comment.getBody();
    this.author = author;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public ProfileData getAuthor() {
    return author;
  }

  public void setAuthor(ProfileData author) {
    this.author = author;
  }
}
