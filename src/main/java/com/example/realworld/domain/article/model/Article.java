package com.example.realworld.domain.article.model;

import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Article {

  private String id;
  private String slug;
  private String title;
  private String description;
  private String body;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private User author;
  private List<Tag> tags;

  public Article() {
    this.tags = new LinkedList<>();
  }

  //  @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  //  private List<Comment> comments;
  //
  //  @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  //  private List<ArticlesTags> tags;
  //
  //  @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  //  private List<ArticlesUsers> favorites;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public User getAuthor() {
    return author;
  }

  public void setAuthor(User author) {
    this.author = author;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }
}
