package com.example.realworld.domain.user.model;

public interface CryptographyProvider {
  String hashPassword(String plainPassword);

  boolean isPasswordValid(String plainText, String hashed);
}
