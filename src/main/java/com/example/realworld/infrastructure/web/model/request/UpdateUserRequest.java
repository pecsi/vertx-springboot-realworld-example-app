package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.domain.constants.ValidationMessages;
import com.example.realworld.domain.entity.persistent.User;
import com.example.realworld.infrastructure.web.validation.constraint.AtLeastOneFieldMustBeNotNull;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@JsonRootName("user")
@AtLeastOneFieldMustBeNotNull
public class UpdateUserRequest {

  @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
  private String username;

  private String bio;
  private String image;
  @Email private String email;

  public User toUser(Long id) {
    User user = new User();
    user.setId(id);
    user.setUsername(this.username);
    user.setBio(this.bio);
    user.setImage(this.image);
    user.setEmail(this.email);
    return user;
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
