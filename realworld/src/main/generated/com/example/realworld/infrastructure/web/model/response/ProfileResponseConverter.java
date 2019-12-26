package com.example.realworld.infrastructure.web.model.response;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.response.ProfileResponse}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.response.ProfileResponse} original class using Vert.x codegen.
 */
public class ProfileResponseConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ProfileResponse obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "bio":
          if (member.getValue() instanceof String) {
            obj.setBio((String)member.getValue());
          }
          break;
        case "following":
          if (member.getValue() instanceof Boolean) {
            obj.setFollowing((Boolean)member.getValue());
          }
          break;
        case "image":
          if (member.getValue() instanceof String) {
            obj.setImage((String)member.getValue());
          }
          break;
        case "username":
          if (member.getValue() instanceof String) {
            obj.setUsername((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(ProfileResponse obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(ProfileResponse obj, java.util.Map<String, Object> json) {
    if (obj.getBio() != null) {
      json.put("bio", obj.getBio());
    }
    json.put("following", obj.isFollowing());
    if (obj.getImage() != null) {
      json.put("image", obj.getImage());
    }
    if (obj.getUsername() != null) {
      json.put("username", obj.getUsername());
    }
  }
}
