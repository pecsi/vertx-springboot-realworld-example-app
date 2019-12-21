package com.example.realworld;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class AbstractIntegrationTest {

  protected static ConfigurableApplicationContext configurableApplicationContext =
      getApplicationContext();

  private static ConfigurableApplicationContext getApplicationContext() {
    if (configurableApplicationContext != null) {
      return configurableApplicationContext;
    } else {
      return SpringApplication.run(RealworldApplication.class);
    }
  }
}
