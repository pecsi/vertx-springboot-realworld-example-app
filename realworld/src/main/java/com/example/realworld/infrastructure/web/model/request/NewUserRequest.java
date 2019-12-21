package com.example.realworld.infrastructure.web.model.request;

import com.example.realworld.application.constants.ValidationMessages;
import com.example.realworld.domain.user.model.NewUser;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@JsonRootName("user")
public class NewUserRequest {

  @NotBlank(message = ValidationMessages.USERNAME_MUST_BE_NOT_BLANK)
  private String username;

  @Email
  @NotBlank(message = ValidationMessages.EMAIL_MUST_BE_NOT_BLANK)
  private String email;

  @NotBlank(message = ValidationMessages.PASSWORD_MUST_BE_NOT_BLANK)
  private String password;

  public NewUser toNewUser() {
    NewUser newUser = new NewUser();
    newUser.setUsername(this.username);
    newUser.setEmail(this.email);
    newUser.setPassword(this.password);
    return newUser;
  }
}
