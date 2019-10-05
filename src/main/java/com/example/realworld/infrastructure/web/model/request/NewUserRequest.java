package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.domain.constants.ValidationMessages;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@JsonRootName("user")
public class NewUserRequest {

  @NotBlank(message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
  private String username;

  @Email
  @NotBlank(message = ValidationMessages.EMAIL_MUST_BE_NOT_BLANK)
  private String email;

  @NotBlank(message = ValidationMessages.PASSWORD_MUST_BE_NOT_BLANK)
  private String password;

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
