package com.example.realworld.domain.tag.model;

import io.reactivex.Single;

import java.util.List;
import java.util.Optional;

public interface TagRepository {
  Single<Tag> store(Tag tag);

  Single<Long> countByName(String name);

  Single<Optional<Tag>> findByName(String name);

  Single<List<Tag>> findAll();
}
