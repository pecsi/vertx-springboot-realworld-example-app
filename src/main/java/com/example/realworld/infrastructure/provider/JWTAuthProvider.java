package com.example.realworld.infrastructure.provider;

import com.example.realworld.domain.user.model.TokenProvider;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JWTAuthProvider implements TokenProvider {

  private JWTAuth jwtAuth;

  public JWTAuthProvider(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  @Override
  public String generateToken(String id) {
    return jwtAuth.generateToken(
        new JsonObject()
            .put("sub", id)
            .put("complementary-subscription", UUID.randomUUID().toString()));
  }
}
