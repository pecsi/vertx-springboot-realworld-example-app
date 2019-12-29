package com.example.realworld.application;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.ArticleRepository;
import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.domain.user.model.FollowedUsersRepository;
import com.example.realworld.domain.user.model.User;
import io.reactivex.Single;

import java.util.List;
import java.util.Optional;

public class ArticleServiceImpl extends ApplicationService implements ArticleService {

  private static final int DEFAULT_LIMIT = 20;

  private ArticleRepository articleRepository;
  private FollowedUsersRepository followedUsersRepository;

  public ArticleServiceImpl(
      ArticleRepository articleRepository, FollowedUsersRepository followedUsersRepository) {
    this.articleRepository = articleRepository;
    this.followedUsersRepository = followedUsersRepository;
  }

  @Override
  public Single<Optional<List<Article>>> findRecentArticles(
      String currentUserId, int offset, int limit) {
    return followedUsersRepository.findRecentArticles(currentUserId, offset, getLimit(limit));
  }

  @Override
  public Single<Article> create(String title, String description, String body, User author) {
    return null;
  }

  private int getLimit(int limit) {
    return limit > 0 ? limit : DEFAULT_LIMIT;
  }
}
