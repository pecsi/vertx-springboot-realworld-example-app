package com.example.realworld.domain.user.model;

public interface HashProvider {
  String hashPassword(String plainPassword);

  boolean isPasswordValid(String plainText, String hashed);
}
