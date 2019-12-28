package com.example.realworld.domain.user.model;

public interface TokenProvider {
  String generateToken(String id);
}
