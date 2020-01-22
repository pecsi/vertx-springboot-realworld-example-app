package com.example.realworld.infrastructure.web.model.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@JsonRootName("comment")
@DataObject(generateConverter = true)
public class NewCommentRequest {

  private String body;

  public NewCommentRequest() {}

  public NewCommentRequest(JsonObject jsonObject) {
    NewCommentRequestConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    NewCommentRequestConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
