package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.application.data.CommentData;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@JsonRootName("comment")
@DataObject(generateConverter = true)
public class CommentResponse {

  private static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  private String id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern)
  private String createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = datePattern)
  private String updatedAt;

  private String body;
  private ProfileResponse author;

  public CommentResponse() {}

  public CommentResponse(JsonObject jsonObject) {
    CommentResponseConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    CommentResponseConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public CommentResponse(CommentData commentData) {
    this.id = commentData.getId();
    this.createdAt = ParserUtils.format(commentData.getCreatedAt(), datePattern);
    this.updatedAt = ParserUtils.format(commentData.getUpdatedAt(), datePattern);
    this.body = commentData.getBody();
    this.author = new ProfileResponse(commentData.getAuthor());
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public ProfileResponse getAuthor() {
    return author;
  }

  public void setAuthor(ProfileResponse author) {
    this.author = author;
  }
}
