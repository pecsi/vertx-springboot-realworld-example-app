package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.Comment;
import com.example.realworld.domain.article.model.CommentRepository;
import com.example.realworld.infrastructure.persistence.statement.CommentStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CommentRepositoryJDBC extends JDBCRepository implements CommentRepository {

  private JDBCClient jdbcClient;
  private CommentStatements commentStatements;

  public CommentRepositoryJDBC(JDBCClient jdbcClient, CommentStatements commentStatements) {
    this.jdbcClient = jdbcClient;
    this.commentStatements = commentStatements;
  }

  @Override
  public Single<Comment> store(Comment comment) {
    comment.setId(UUID.randomUUID().toString());
    Statement<JsonArray> storeCommentStatement = commentStatements.store(comment);
    return jdbcClient
        .rxUpdateWithParams(storeCommentStatement.sql(), storeCommentStatement.params())
        .map(updateResult -> comment);
  }

  @Override
  public Completable deleteByCommentIdAndAuthorId(String commentId, String authorId) {
    Statement<JsonArray> deleteByCommentIdAndAuthorIdStatement =
        commentStatements.deleteByCommentIdAndAuthorId(commentId, authorId);
    return jdbcClient
        .rxUpdateWithParams(
            deleteByCommentIdAndAuthorIdStatement.sql(),
            deleteByCommentIdAndAuthorIdStatement.params())
        .flatMapCompletable(updateResult -> Completable.complete());
  }

  @Override
  public Single<List<Comment>> findCommentsByArticleId(String articleId) {
    Statement<JsonArray> findCommentsByArticleIdStatement =
        commentStatements.findCommentsByArticleId(articleId);
    return jdbcClient
        .rxQueryWithParams(
            findCommentsByArticleIdStatement.sql(), findCommentsByArticleIdStatement.params())
        .map(ParserUtils::toCommentList);
  }
}
