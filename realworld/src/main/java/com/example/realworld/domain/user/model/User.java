package com.example.realworld.domain.user.model;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class User {

  private String id;
  private String username;
  private String bio;
  private String image;
  private String password;
  private String email;
  private String token;
}
