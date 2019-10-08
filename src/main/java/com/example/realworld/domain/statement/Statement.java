package com.example.realworld.domain.statement;

public interface Statement<T> {

  String sql();

  T params();
}
