package com.example.realworld;

import org.junit.jupiter.api.Test;

class RealworldApplicationTests extends AbstractIntegrationTest {

  @Test
  void contextLoads() {
    configurableApplicationContext.getBean("defaultObjectMapper");
  }
}
