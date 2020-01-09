package com.example.realworld.infrastructure.web.model.response;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.response.ArticlesFeedResponse}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.response.ArticlesFeedResponse} original class using Vert.x codegen.
 */
public class ArticlesFeedResponseConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ArticlesFeedResponse obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "articles":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<com.example.realworld.infrastructure.web.model.response.ArticleResponse> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof JsonObject)
                list.add(new com.example.realworld.infrastructure.web.model.response.ArticleResponse((JsonObject)item));
            });
            obj.setArticles(list);
          }
          break;
        case "articlesCount":
          if (member.getValue() instanceof Number) {
            obj.setArticlesCount(((Number)member.getValue()).longValue());
          }
          break;
      }
    }
  }

  public static void toJson(ArticlesFeedResponse obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(ArticlesFeedResponse obj, java.util.Map<String, Object> json) {
    if (obj.getArticles() != null) {
      JsonArray array = new JsonArray();
      obj.getArticles().forEach(item -> array.add(item.toJson()));
      json.put("articles", array);
    }
    if (obj.getArticlesCount() != null) {
      json.put("articlesCount", obj.getArticlesCount());
    }
  }
}
