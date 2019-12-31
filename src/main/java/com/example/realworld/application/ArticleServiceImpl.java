package com.example.realworld.application;

import com.example.realworld.domain.article.exception.SlugAlreadyExistsException;
import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.ArticleRepository;
import com.example.realworld.domain.article.model.NewArticle;
import com.example.realworld.domain.article.model.SlugProvider;
import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.domain.tag.exception.TagNotFoundException;
import com.example.realworld.domain.tag.model.NewTag;
import com.example.realworld.domain.tag.service.TagService;
import com.example.realworld.domain.user.model.FollowedUsersRepository;
import com.example.realworld.domain.user.model.ModelValidator;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleServiceImpl extends ApplicationService implements ArticleService {

  private static final int DEFAULT_LIMIT = 20;

  private ArticleRepository articleRepository;
  private FollowedUsersRepository followedUsersRepository;
  private SlugProvider slugProvider;
  private ModelValidator modelValidator;
  private TagService tagService;

  public ArticleServiceImpl(
      ArticleRepository articleRepository,
      FollowedUsersRepository followedUsersRepository,
      SlugProvider slugProvider,
      ModelValidator modelValidator,
      TagService tagService) {
    this.articleRepository = articleRepository;
    this.followedUsersRepository = followedUsersRepository;
    this.slugProvider = slugProvider;
    this.modelValidator = modelValidator;
    this.tagService = tagService;
  }

  @Override
  public Single<List<Article>> findRecentArticles(String currentUserId, int offset, int limit) {
    return followedUsersRepository.findRecentArticles(currentUserId, offset, getLimit(limit));
  }

  @Override
  public Single<Article> create(NewArticle newArticle) {
    modelValidator.validate(newArticle);
    Article article = createFromNewArticle(newArticle);
    return validSlug(article.getSlug())
        .andThen(configTags(article, newArticle.getTags()))
        .andThen(articleRepository.store(article));
  }

  @Override
  public Single<Long> totalUserArticlesFollowed(String currentUserId) {
    return followedUsersRepository.totalUserArticlesFollowed(currentUserId);
  }

  private Completable validSlug(String slug) {
    return isSlugAlreadyExists(slug)
        .flatMapCompletable(
            isSlugAlreadyExists -> {
              if (isSlugAlreadyExists) {
                throw new SlugAlreadyExistsException();
              }
              return Completable.complete();
            });
  }

  private Completable configTags(Article article, List<NewTag> newTags) {
    return Flowable.fromIterable(newTags)
        .flatMapSingle(newTag -> tagService.findTagByName(newTag.getName()))
        .flatMapCompletable(
            tag -> {
              article.getTags().add(tag.orElseThrow(TagNotFoundException::new));
              return Completable.complete();
            });
  }

  private Single<Boolean> isSlugAlreadyExists(String slug) {
    return articleRepository.countBySlug(slug).map(this::isCountResultGreaterThanZero);
  }

  private Article createFromNewArticle(NewArticle newArticle) {
    Article article = new Article();
    article.setTitle(newArticle.getTitle());
    article.setSlug(slugProvider.slugify(article.getTitle()));
    article.setDescription(newArticle.getDescription());
    article.setBody(newArticle.getBody());
    article.setAuthor(newArticle.getAuthor());
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    return article;
  }

  private int getLimit(int limit) {
    return limit > 0 ? limit : DEFAULT_LIMIT;
  }
}
