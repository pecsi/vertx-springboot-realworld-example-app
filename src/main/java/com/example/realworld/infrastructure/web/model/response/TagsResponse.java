package com.example.realworld.infrastructure.web.model.response;

import com.example.realworld.domain.tag.model.Tag;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

@DataObject(generateConverter = true)
public class TagsResponse {

  private List<String> tags;

  public TagsResponse() {}

  public TagsResponse(JsonObject jsonObject) {
    TagsResponseConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    TagsResponseConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public TagsResponse(List<Tag> tags) {
    this.tags = tags.stream().map(Tag::getName).collect(Collectors.toList());
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }
}
