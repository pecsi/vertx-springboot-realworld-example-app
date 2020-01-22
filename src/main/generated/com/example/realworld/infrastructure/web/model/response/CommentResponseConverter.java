package com.example.realworld.infrastructure.web.model.response;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.response.CommentResponse}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.response.CommentResponse} original class using Vert.x codegen.
 */
public class CommentResponseConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CommentResponse obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "author":
          if (member.getValue() instanceof JsonObject) {
            obj.setAuthor(new com.example.realworld.infrastructure.web.model.response.ProfileResponse((JsonObject)member.getValue()));
          }
          break;
        case "body":
          if (member.getValue() instanceof String) {
            obj.setBody((String)member.getValue());
          }
          break;
        case "createdAt":
          if (member.getValue() instanceof String) {
            obj.setCreatedAt((String)member.getValue());
          }
          break;
        case "id":
          if (member.getValue() instanceof String) {
            obj.setId((String)member.getValue());
          }
          break;
        case "updatedAt":
          if (member.getValue() instanceof String) {
            obj.setUpdatedAt((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(CommentResponse obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(CommentResponse obj, java.util.Map<String, Object> json) {
    if (obj.getAuthor() != null) {
      json.put("author", obj.getAuthor().toJson());
    }
    if (obj.getBody() != null) {
      json.put("body", obj.getBody());
    }
    if (obj.getCreatedAt() != null) {
      json.put("createdAt", obj.getCreatedAt());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getUpdatedAt() != null) {
      json.put("updatedAt", obj.getUpdatedAt());
    }
  }
}
