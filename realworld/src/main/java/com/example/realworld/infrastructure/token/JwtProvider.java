package com.example.realworld.infrastructure.token;

import com.example.realworld.domain.user.model.TokenProvider;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider implements TokenProvider {
  @Override
  public String generateToken(String id) {
    return null;
  }
}
