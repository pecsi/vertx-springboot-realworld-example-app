package com.example.realworld.infrastructure.web.model.response;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.response.ArticleResponse}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.response.ArticleResponse} original class using Vert.x codegen.
 */
public class ArticleResponseConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ArticleResponse obj) {
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
        case "description":
          if (member.getValue() instanceof String) {
            obj.setDescription((String)member.getValue());
          }
          break;
        case "favorited":
          if (member.getValue() instanceof Boolean) {
            obj.setFavorited((Boolean)member.getValue());
          }
          break;
        case "favoritesCount":
          if (member.getValue() instanceof Number) {
            obj.setFavoritesCount(((Number)member.getValue()).intValue());
          }
          break;
        case "slug":
          if (member.getValue() instanceof String) {
            obj.setSlug((String)member.getValue());
          }
          break;
        case "tagList":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<java.lang.String> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof String)
                list.add((String)item);
            });
            obj.setTagList(list);
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

  public static void toJson(ArticleResponse obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(ArticleResponse obj, java.util.Map<String, Object> json) {
    if (obj.getAuthor() != null) {
      json.put("author", obj.getAuthor().toJson());
    }
    if (obj.getBody() != null) {
      json.put("body", obj.getBody());
    }
    if (obj.getDescription() != null) {
      json.put("description", obj.getDescription());
    }
    json.put("favorited", obj.isFavorited());
    json.put("favoritesCount", obj.getFavoritesCount());
    if (obj.getSlug() != null) {
      json.put("slug", obj.getSlug());
    }
    if (obj.getTagList() != null) {
      JsonArray array = new JsonArray();
      obj.getTagList().forEach(item -> array.add(item));
      json.put("tagList", array);
    }
    if (obj.getTitle() != null) {
      json.put("title", obj.getTitle());
    }
  }
}
