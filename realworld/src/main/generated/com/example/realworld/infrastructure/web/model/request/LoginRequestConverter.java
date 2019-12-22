package com.example.realworld.infrastructure.web.model.request;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link com.example.realworld.infrastructure.web.model.request.LoginRequest}.
 * NOTE: This class has been automatically generated from the {@link com.example.realworld.infrastructure.web.model.request.LoginRequest} original class using Vert.x codegen.
 */
public class LoginRequestConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, LoginRequest obj) {
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
      }
    }
  }

  public static void toJson(LoginRequest obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(LoginRequest obj, java.util.Map<String, Object> json) {
    if (obj.getEmail() != null) {
      json.put("email", obj.getEmail());
    }
    if (obj.getPassword() != null) {
      json.put("password", obj.getPassword());
    }
  }
}
