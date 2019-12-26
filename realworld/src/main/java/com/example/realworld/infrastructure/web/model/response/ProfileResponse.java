package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.domain.profile.model.Profile;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@JsonRootName("profile")
@DataObject(generateConverter = true)
public class ProfileResponse {

  private String username;
  private String bio;
  private String image;
  private boolean following;

  public ProfileResponse() {}

  public ProfileResponse(JsonObject jsonObject) {
    ProfileResponseConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    ProfileResponseConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public ProfileResponse(Profile profile) {
    this.username = profile.getUsername();
    this.bio = profile.getBio();
    this.image = profile.getImage();
    this.following = profile.isFollowing();
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

  public boolean isFollowing() {
    return following;
  }

  public void setFollowing(boolean following) {
    this.following = following;
  }
}
