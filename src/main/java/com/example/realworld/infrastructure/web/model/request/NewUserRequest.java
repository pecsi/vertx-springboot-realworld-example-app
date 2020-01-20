package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.domain.user.model.NewUser;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@JsonRootName("user")
@DataObject(generateConverter = true)
public class NewUserRequest {

  private String username;

  private String email;

  private String password;

  public NewUserRequest() {}

  public NewUserRequest(JsonObject jsonObject) {
    NewUserRequestConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    NewUserRequestConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public NewUser toNewUser() {
    NewUser newUser = new NewUser();
    newUser.setUsername(this.username);
    newUser.setEmail(this.email);
    newUser.setPassword(this.password);
    return newUser;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
