package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.domain.entity.persistent.User;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public class UserResponse {

  private String username;
  private String bio;
  private String image;
  private String email;
  private String token;

  public UserResponse(User user) {
    this.username = user.getUsername();
    this.bio = user.getBio();
    this.image = user.getImage();
    this.email = user.getEmail();
    this.token = user.getToken();
  }
}
