package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.user.model.User;
import com.example.realworld.domain.user.model.UserRepository;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.statement.UserStatements;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryJDBC implements UserRepository {

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
    Statement<JsonArray> counterByUsernameStatement =
        userStatements.counterBy("username", username);
    return jdbcClient
        .rxQueryWithParams(counterByUsernameStatement.sql(), counterByUsernameStatement.params())
        .map(this::getCounterFromResultSet);
  }

  @Override
  public Single<Long> countByEmail(String email) {
    Statement<JsonArray> counterByEmailStatement = userStatements.counterBy("email", email);
    return jdbcClient
        .rxQueryWithParams(counterByEmailStatement.sql(), counterByEmailStatement.params())
        .map(this::getCounterFromResultSet);
  }

  @Override
  public Single<User> findById(String id) {
    Statement<JsonArray> findByIdStatement = userStatements.findById(id);
    return jdbcClient
        .rxQueryWithParams(findByIdStatement.sql(), findByIdStatement.params())
        .map(ParserUtils::toUser);
  }

  protected Long getCounterFromResultSet(ResultSet resultSet) {
    return resultSet.getRows().get(0).getLong("COUNT(*)");
  }
}
