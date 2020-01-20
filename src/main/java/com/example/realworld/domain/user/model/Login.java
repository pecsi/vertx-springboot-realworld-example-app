package com.example.realworld.domain.user.model;

import com.example.realworld.application.constants.ValidationMessages;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class Login {

  @Email
  @NotBlank(message = ValidationMessages.EMAIL_MUST_BE_NOT_BLANK)
  private String email;

  @NotBlank(message = ValidationMessages.PASSWORD_MUST_BE_NOT_BLANK)
  private String password;

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
