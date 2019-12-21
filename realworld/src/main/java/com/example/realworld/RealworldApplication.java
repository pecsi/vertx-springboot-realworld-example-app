package com.example.realworld;

import com.example.realworld.infrastructure.vertx.verticle.VertxConfiguration;
import io.vertx.reactivex.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties(VertxConfiguration.class)
public class RealworldApplication {

  @Autowired private Vertx vertx;

  public static void main(String[] args) {
    SpringApplication.run(RealworldApplication.class, args);
  }

  @PostConstruct
  public void deployMainVerticle() {}
}
