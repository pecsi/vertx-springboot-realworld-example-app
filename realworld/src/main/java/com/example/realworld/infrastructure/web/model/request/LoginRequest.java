package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.application.constants.ValidationMessages;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import javax.validation.constraints.NotBlank;

@JsonRootName("user")
@DataObject(generateConverter = true)
public class LoginRequest {

  @NotBlank(message = ValidationMessages.EMAIL_MUST_BE_NOT_BLANK)
  private String email;

  @NotBlank(message = ValidationMessages.PASSWORD_MUST_BE_NOT_BLANK)
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
