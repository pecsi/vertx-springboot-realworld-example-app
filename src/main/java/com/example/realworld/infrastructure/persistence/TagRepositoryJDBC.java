package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.tag.model.TagRepository;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.statement.TagStatements;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TagRepositoryJDBC extends JDBCRepository implements TagRepository {

  private JDBCClient jdbcClient;
  private TagStatements tagStatements;

  public TagRepositoryJDBC(JDBCClient jdbcClient, TagStatements tagStatements) {
    this.jdbcClient = jdbcClient;
    this.tagStatements = tagStatements;
  }

  @Override
  public Single<Tag> store(Tag tag) {
    tag.setId(UUID.randomUUID().toString());
    Statement<JsonArray> storeTagStatement = tagStatements.store(tag);
    return jdbcClient
        .rxUpdateWithParams(storeTagStatement.sql(), storeTagStatement.params())
        .map(updateResult -> tag);
  }

  @Override
  public Single<Long> countByName(String name) {
    Statement<JsonArray> countByNameStatement = tagStatements.countBy("name", name);
    return jdbcClient
        .rxQueryWithParams(countByNameStatement.sql(), countByNameStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Optional<Tag>> findByName(String name) {
    Statement<JsonArray> findTagByNameStatement = tagStatements.findTagByName(name);
    return jdbcClient
        .rxQueryWithParams(findTagByNameStatement.sql(), findTagByNameStatement.params())
        .map(ParserUtils::toTagOptional);
  }

  @Override
  public Single<List<Tag>> findAll() {
    String findAllStatement = tagStatements.findAll();
    return jdbcClient.rxQuery(findAllStatement).map(ParserUtils::toTagList);
  }
}
