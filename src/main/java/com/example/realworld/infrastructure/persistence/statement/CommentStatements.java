package com.example.realworld.infrastructure.persistence.statement;

import com.example.realworld.domain.article.model.Comment;
import io.vertx.core.json.JsonArray;

public interface CommentStatements {
  Statement<JsonArray> store(Comment comment);

  Statement<JsonArray> deleteByCommentIdAndAuthorId(String commentId, String authorId);

  Statement<JsonArray> findCommentsByArticleId(String articleId);
}
