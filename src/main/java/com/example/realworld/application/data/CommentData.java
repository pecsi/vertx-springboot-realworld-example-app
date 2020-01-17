package com.example.realworld.application.data;

import java.time.LocalDateTime;

public class CommentData {

  private Long id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String body;
  private ProfileData author;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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
