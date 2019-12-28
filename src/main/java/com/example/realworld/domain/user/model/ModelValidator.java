package com.example.realworld.domain.user.model;

public interface ModelValidator {
  <T> void validate(T model);
}
