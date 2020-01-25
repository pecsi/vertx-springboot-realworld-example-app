package com.example.realworld.infrastructure.web.model.response;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.response.CommentsResponse}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.response.CommentsResponse} original class using Vert.x codegen.
 */
public class CommentsResponseConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, CommentsResponse obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "comments":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<com.example.realworld.infrastructure.web.model.response.CommentResponse> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof JsonObject)
                list.add(new com.example.realworld.infrastructure.web.model.response.CommentResponse((JsonObject)item));
            });
            obj.setComments(list);
          }
          break;
      }
    }
  }

  public static void toJson(CommentsResponse obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(CommentsResponse obj, java.util.Map<String, Object> json) {
    if (obj.getComments() != null) {
      JsonArray array = new JsonArray();
      obj.getComments().forEach(item -> array.add(item.toJson()));
      json.put("comments", array);
    }
  }
}
