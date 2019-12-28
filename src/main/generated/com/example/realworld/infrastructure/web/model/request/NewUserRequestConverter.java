package com.example.realworld.infrastructure.web.model.request;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.request.NewUserRequest}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.request.NewUserRequest} original class using Vert.x codegen.
 */
public class NewUserRequestConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, NewUserRequest obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "email":
          if (member.getValue() instanceof String) {
            obj.setEmail((String)member.getValue());
          }
          break;
        case "password":
          if (member.getValue() instanceof String) {
            obj.setPassword((String)member.getValue());
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

  public static void toJson(NewUserRequest obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(NewUserRequest obj, java.util.Map<String, Object> json) {
    if (obj.getEmail() != null) {
      json.put("email", obj.getEmail());
    }
    if (obj.getPassword() != null) {
      json.put("password", obj.getPassword());
    }
    if (obj.getUsername() != null) {
      json.put("username", obj.getUsername());
    }
  }
}
