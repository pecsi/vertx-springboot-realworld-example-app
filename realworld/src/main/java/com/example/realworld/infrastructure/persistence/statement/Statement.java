package com.example.realworld.infrastructure.persistence.statement;

public interface Statement<T> {

  String sql();

  T params();
}
