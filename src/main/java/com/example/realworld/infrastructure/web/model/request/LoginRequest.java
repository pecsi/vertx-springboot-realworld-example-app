package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.domain.user.model.Login;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@JsonRootName("user")
@DataObject(generateConverter = true)
public class LoginRequest {

  private String email;

  private String password;

  public LoginRequest() {}

  public LoginRequest(JsonObject jsonObject) {
    LoginRequestConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    LoginRequestConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public Login toLogin() {
    Login login = new Login();
    login.setEmail(this.email);
    login.setPassword(this.password);
    return login;
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
