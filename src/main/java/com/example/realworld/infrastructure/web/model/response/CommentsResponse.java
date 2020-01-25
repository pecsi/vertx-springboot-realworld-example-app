package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.application.data.CommentData;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@DataObject(generateConverter = true)
public class CommentsResponse {

  private List<CommentResponse> comments;

  public CommentsResponse() {}

  public CommentsResponse(JsonObject jsonObject) {
    CommentsResponseConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    CommentsResponseConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public CommentsResponse(List<CommentData> comments) {
    this.comments = comments.stream().map(CommentResponse::new).collect(Collectors.toList());
  }

  public List<CommentResponse> getComments() {
    return comments;
  }

  public void setComments(List<CommentResponse> comments) {
    this.comments = comments;
  }
}
