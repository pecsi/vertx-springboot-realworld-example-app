package com.example.realworld.domain.article.service;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.NewArticle;
import io.reactivex.Single;

import java.util.List;

public interface ArticleService {

  Single<List<Article>> findRecentArticles(String currentUserId, int offset, int limit);

  Single<Article> create(NewArticle newArticle);

  Single<Long> totalUserArticlesFollowed(String currentUserId);

  Single<Boolean> isFavorited(String articleId, String currentUserId);

  Single<Long> favoritesCount(String articleId);

  //  Articles findArticles(
  //    int offset,
  //    int limit,
  //    Long loggedUserId,
  //    List<String> tags,
  //    List<String> authors,
  //    List<String> favorited);
  //
  //  Article create(
  //    String title, String description, String body, List<String> tagList, Long authorId);
  //
  //  Article findBySlug(String slug);
  //
  //  Article update(String slug, String title, String description, String body, Long authorId);
  //
  //  void delete(String slug, Long authorId);
  //
  //  List<Comment> findCommentsBySlug(String slug, Long loggedUserId);
  //
  //  Comment createComment(String slug, String body, Long commentAuthorId);
  //
  //  void deleteComment(String slug, Long commentId, Long loggedUserId);
  //
  //  Article favoriteArticle(String slug, Long loggedUserId);
  //
  //  Article unfavoriteArticle(String slug, Long loggedUserId);

}
