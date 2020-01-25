package com.example.realworld.infrastructure.web.model.response;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.response.TagsResponse}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.response.TagsResponse} original class using Vert.x codegen.
 */
public class TagsResponseConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, TagsResponse obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
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
      }
    }
  }

  public static void toJson(TagsResponse obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(TagsResponse obj, java.util.Map<String, Object> json) {
    if (obj.getTags() != null) {
      JsonArray array = new JsonArray();
      obj.getTags().forEach(item -> array.add(item));
      json.put("tags", array);
    }
  }
}
