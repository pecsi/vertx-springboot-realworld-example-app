package com.example.realworld.application;

import java.util.Optional;

class ApplicationService {

  <R> Optional<R> toOptional(R object) {
    return Optional.ofNullable(object);
  }

  boolean isCountResultGreaterThanZero(Long countResult) {
    return countResult > 0;
  }
}
