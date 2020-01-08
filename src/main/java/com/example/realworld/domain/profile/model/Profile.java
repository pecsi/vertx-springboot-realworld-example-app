package com.example.realworld.domain.profile.model;

import com.example.realworld.domain.user.model.User;

public class Profile {

  private String username;
  private String bio;
  private String image;
  private boolean following;

  public Profile(User user, boolean isFollowing) {
    this(user);
    this.following = isFollowing;
  }

  public Profile(User user) {
    this.username = user.getUsername();
    this.bio = user.getBio();
    this.image = user.getImage();
  }

  public Profile(String username) {
    this.username = username;
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
