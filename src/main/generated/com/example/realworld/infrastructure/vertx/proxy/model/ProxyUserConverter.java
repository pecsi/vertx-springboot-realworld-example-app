package com.example.realworld.infrastructure.vertx.proxy.model;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.vertx.proxy.model.ProxyUser}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.vertx.proxy.model.ProxyUser} original class using Vert.x codegen.
 */
public class ProxyUserConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, ProxyUser obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "bio":
          if (member.getValue() instanceof String) {
            obj.setBio((String)member.getValue());
          }
          break;
        case "email":
          if (member.getValue() instanceof String) {
            obj.setEmail((String)member.getValue());
          }
          break;
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).longValue());
          }
          break;
        case "image":
          if (member.getValue() instanceof String) {
            obj.setImage((String)member.getValue());
          }
          break;
        case "password":
          if (member.getValue() instanceof String) {
            obj.setPassword((String)member.getValue());
          }
          break;
        case "token":
          if (member.getValue() instanceof String) {
            obj.setToken((String)member.getValue());
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

  public static void toJson(ProxyUser obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(ProxyUser obj, java.util.Map<String, Object> json) {
    if (obj.getBio() != null) {
      json.put("bio", obj.getBio());
    }
    if (obj.getEmail() != null) {
      json.put("email", obj.getEmail());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getImage() != null) {
      json.put("image", obj.getImage());
    }
    if (obj.getPassword() != null) {
      json.put("password", obj.getPassword());
    }
    if (obj.getToken() != null) {
      json.put("token", obj.getToken());
    }
    if (obj.getUsername() != null) {
      json.put("username", obj.getUsername());
    }
  }
}
