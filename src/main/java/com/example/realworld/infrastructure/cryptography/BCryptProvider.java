package com.example.realworld.infrastructure.cryptography;

import com.example.realworld.domain.user.model.HashProvider;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptProvider implements HashProvider {
  @Override
  public String hashPassword(String plainPassword) {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
  }

  @Override
  public boolean isPasswordValid(String plainText, String hashed) {
    return BCrypt.checkpw(plainText, hashed);
  }
}
