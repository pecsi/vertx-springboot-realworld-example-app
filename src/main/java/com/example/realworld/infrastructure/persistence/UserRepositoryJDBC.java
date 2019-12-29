package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.user.model.User;
import com.example.realworld.domain.user.model.UserRepository;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.statement.UserStatements;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryJDBC extends JDBCRepository implements UserRepository {

  private JDBCClient jdbcClient;
  private UserStatements userStatements;

  public UserRepositoryJDBC(JDBCClient jdbcClient, UserStatements userStatements) {
    this.jdbcClient = jdbcClient;
    this.userStatements = userStatements;
  }

  @Override
  public Single<User> store(User user) {
    Statement<JsonArray> createUserStatement = userStatements.create(user);
    return jdbcClient
        .rxUpdateWithParams(createUserStatement.sql(), createUserStatement.params())
        .flatMap(updateResult -> Single.just(user));
  }

  @Override
  public Single<Long> countByUsername(String username) {
    Statement<JsonArray> countByUsernameStatement = userStatements.countBy("username", username);
    return jdbcClient
        .rxQueryWithParams(countByUsernameStatement.sql(), countByUsernameStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Long> countByUsername(String username, String exclusionId) {
    Statement<JsonArray> countByUsernameStatement =
        userStatements.countBy("username", username, exclusionId);
    return jdbcClient
        .rxQueryWithParams(countByUsernameStatement.sql(), countByUsernameStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Long> countByEmail(String email) {
    Statement<JsonArray> countByEmailStatement = userStatements.countBy("email", email);
    return jdbcClient
        .rxQueryWithParams(countByEmailStatement.sql(), countByEmailStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Long> countByEmail(String email, String excludeUserId) {
    Statement<JsonArray> countByEmailStatement =
        userStatements.countBy("email", email, excludeUserId);
    return jdbcClient
        .rxQueryWithParams(countByEmailStatement.sql(), countByEmailStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Optional<User>> findById(String id) {
    Statement<JsonArray> findByIdStatement = userStatements.findById(id);
    return jdbcClient
        .rxQueryWithParams(findByIdStatement.sql(), findByIdStatement.params())
        .map(ParserUtils::toUserOptional);
  }

  @Override
  public Single<Optional<User>> findUserByEmail(String email) {
    Statement<JsonArray> findByEmailStatement = userStatements.findByEmail(email);
    return jdbcClient
        .rxQueryWithParams(findByEmailStatement.sql(), findByEmailStatement.params())
        .map(ParserUtils::toUserOptional);
  }

  @Override
  public Single<User> update(User user) {
    Statement<JsonArray> updateUserStatement = userStatements.update(user);
    return jdbcClient
        .rxUpdateWithParams(updateUserStatement.sql(), updateUserStatement.params())
        .map(updateResult -> user);
  }

  @Override
  public Single<Optional<User>> findUserByUsername(String username) {
    Statement<JsonArray> findByUsernameStatement = userStatements.findByUsername(username);
    return jdbcClient
        .rxQueryWithParams(findByUsernameStatement.sql(), findByUsernameStatement.params())
        .map(ParserUtils::toUserOptional);
  }
}
