package com.example.realworld.domain.article.model;

import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;

public interface CommentRepository {
  Single<Comment> store(Comment comment);

  Completable deleteByCommentIdAndAuthorId(String commentId, String authorId);

  Single<List<Comment>> findCommentsByArticleId(String articleId);
}
