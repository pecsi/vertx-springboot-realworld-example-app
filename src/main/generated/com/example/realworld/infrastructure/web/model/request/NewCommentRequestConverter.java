package com.example.realworld.infrastructure.web.model.request;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.request.NewCommentRequest}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.request.NewCommentRequest} original class using Vert.x codegen.
 */
public class NewCommentRequestConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, NewCommentRequest obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "body":
          if (member.getValue() instanceof String) {
            obj.setBody((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(NewCommentRequest obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(NewCommentRequest obj, java.util.Map<String, Object> json) {
    if (obj.getBody() != null) {
      json.put("body", obj.getBody());
    }
  }
}
