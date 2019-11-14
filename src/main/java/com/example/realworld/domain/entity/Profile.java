package com.example.realworld.domain.entity;

import com.example.realworld.domain.entity.persistent.User;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Profile {

  private String username;
  private String bio;
  private String image;
  private boolean following;

  public Profile() {}

  public Profile(JsonObject jsonObject) {
    ProfileConverter.fromJson(jsonObject, this);
  }

  public Profile(User user, boolean isFollowing) {
    this.username = user.getUsername();
    this.bio = user.getBio();
    this.image = user.getImage();
    this.following = isFollowing;
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    ProfileConverter.toJson(this, jsonObject);
    return jsonObject;
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
