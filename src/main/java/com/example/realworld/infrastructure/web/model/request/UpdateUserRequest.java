package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.application.constants.ValidationMessages;
import com.example.realworld.domain.user.model.UpdateUser;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@JsonRootName("user")
@DataObject(generateConverter = true)
public class UpdateUserRequest {

  @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
  private String username;

  private String bio;
  private String image;
  @Email private String email;

  public UpdateUserRequest() {}

  public UpdateUserRequest(JsonObject jsonObject) {
    UpdateUserRequestConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    UpdateUserRequestConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public UpdateUser toUpdateUser() {
    UpdateUser updateUser = new UpdateUser();
    updateUser.setUsername(this.username);
    updateUser.setBio(this.bio);
    updateUser.setImage(this.image);
    updateUser.setEmail(this.email);
    return updateUser;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
