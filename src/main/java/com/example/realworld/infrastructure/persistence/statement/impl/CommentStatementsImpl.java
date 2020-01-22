package com.example.realworld.infrastructure.persistence.statement.impl;

import com.example.realworld.domain.article.model.Comment;
import com.example.realworld.infrastructure.persistence.statement.CommentStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.vertx.core.json.JsonArray;
import org.springframework.stereotype.Component;

@Component
public class CommentStatementsImpl implements CommentStatements {

  @Override
  public Statement<JsonArray> store(Comment comment) {

    String sql =
        "INSERT INTO COMMENTS (ID, AUTHOR_ID, ARTICLE_ID, CREATED_AT, UPDATED_AT, BODY) VALUES (?, ?, ?, ?, ?, ?)";

    JsonArray params =
        new JsonArray()
            .add(comment.getId())
            .add(comment.getAuthor().getId())
            .add(comment.getArticle().getId())
            .add(ParserUtils.toTimestamp(comment.getCreatedAt()))
            .add(ParserUtils.toTimestamp(comment.getUpdatedAt()))
            .add(comment.getBody());

    return new JsonArrayStatement(sql, params);
  }

  @Override
  public Statement<JsonArray> deleteByCommentIdAndAuthorIdStatement(
      String commentId, String authorId) {

    String sql = "DELETE FROM COMMENTS WHERE ID = ? AND AUTHOR_ID = ?";

    JsonArray params = new JsonArray().add(commentId).add(authorId);

    return new JsonArrayStatement(sql, params);
  }
}
