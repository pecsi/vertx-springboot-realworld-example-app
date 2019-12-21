package com.example.realworld.infrastructure.cryptography;

import com.example.realworld.domain.user.CryptographyService;
import org.springframework.stereotype.Component;

@Component
public class BCryptService implements CryptographyService {
  @Override
  public String hashPassword(String plainPassword) {
    return null;
  }
}
