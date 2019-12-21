package com.example.realworld.domain.user;

public interface CryptographyService {
  String hashPassword(String plainPassword);
}
