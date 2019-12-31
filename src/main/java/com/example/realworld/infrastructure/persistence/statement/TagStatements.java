package com.example.realworld.infrastructure.persistence.statement;

import com.example.realworld.domain.tag.model.Tag;
import io.vertx.core.json.JsonArray;

public interface TagStatements {
  Statement<JsonArray> store(Tag tag);

  Statement<JsonArray> countBy(String field, String value);

  Statement<JsonArray> findTagByName(String name);
}
