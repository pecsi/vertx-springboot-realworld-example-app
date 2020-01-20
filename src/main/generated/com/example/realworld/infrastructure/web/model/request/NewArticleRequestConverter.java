package com.example.realworld.infrastructure.web.model.request;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.request.NewArticleRequest}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.request.NewArticleRequest} original class using Vert.x codegen.
 */
public class NewArticleRequestConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, NewArticleRequest obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "body":
          if (member.getValue() instanceof String) {
            obj.setBody((String)member.getValue());
          }
          break;
        case "description":
          if (member.getValue() instanceof String) {
            obj.setDescription((String)member.getValue());
          }
          break;
        case "tags":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<java.lang.String> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof String)
                list.add((String)item);
            });
            obj.setTags(list);
          }
          break;
        case "title":
          if (member.getValue() instanceof String) {
            obj.setTitle((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(NewArticleRequest obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(NewArticleRequest obj, java.util.Map<String, Object> json) {
    if (obj.getBody() != null) {
      json.put("body", obj.getBody());
    }
    if (obj.getDescription() != null) {
      json.put("description", obj.getDescription());
    }
    if (obj.getTags() != null) {
      JsonArray array = new JsonArray();
      obj.getTags().forEach(item -> array.add(item));
      json.put("tags", array);
    }
    if (obj.getTitle() != null) {
      json.put("title", obj.getTitle());
    }
  }
}
