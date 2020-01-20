package com.example.realworld.application;

import com.example.realworld.application.data.ArticleData;
import com.example.realworld.application.data.ArticlesData;
import com.example.realworld.application.data.ProfileData;
import com.example.realworld.domain.article.exception.ArticleNotFoundException;
import com.example.realworld.domain.article.model.*;
import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.domain.profile.model.UsersFollowedRepository;
import com.example.realworld.domain.profile.service.ProfileService;
import com.example.realworld.domain.tag.exception.TagNotFoundException;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.tag.service.TagService;
import com.example.realworld.domain.user.model.ModelValidator;
import com.example.realworld.domain.user.model.User;
import com.example.realworld.domain.user.service.UserService;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArticleServiceImpl extends ApplicationService implements ArticleService {

  private static final int DEFAULT_LIMIT = 20;

  private ArticleRepository articleRepository;
  private UsersFollowedRepository usersFollowedRepository;
  private FavoritesRepository favoritesRepository;
  private SlugProvider slugProvider;
  private ModelValidator modelValidator;
  private ProfileService profileService;
  private TagService tagService;
  private UserService userService;

  public ArticleServiceImpl(
      ArticleRepository articleRepository,
      UsersFollowedRepository usersFollowedRepository,
      FavoritesRepository favoritesRepository,
      SlugProvider slugProvider,
      ModelValidator modelValidator,
      ProfileService profileService,
      TagService tagService,
      UserService userService) {
    this.articleRepository = articleRepository;
    this.usersFollowedRepository = usersFollowedRepository;
    this.favoritesRepository = favoritesRepository;
    this.slugProvider = slugProvider;
    this.modelValidator = modelValidator;
    this.profileService = profileService;
    this.tagService = tagService;
    this.userService = userService;
  }

  @Override
  public Single<ArticlesData> findRecentArticles(String currentUserId, int offset, int limit) {
    return usersFollowedRepository
        .findRecentArticles(currentUserId, offset, getLimit(limit))
        .flattenAsFlowable(articles -> articles)
        .flatMapSingle(article -> toArticleData(article, currentUserId))
        .sorted(articleComparator())
        .toList()
        .flatMap(
            articles ->
                totalUserArticlesFollowed(currentUserId)
                    .map(articlesCount -> new ArticlesData(articles, articlesCount)));
  }

  private Single<ArticleData> toArticleData(Article article, String currentUserId) {
    return isFavorited(article.getId(), currentUserId)
        .flatMap(
            isFavorited ->
                favoritesCount(article.getId())
                    .flatMap(
                        favoritesCount ->
                            tagService
                                .findTagsByArticle(article.getId())
                                .flatMap(
                                    tags ->
                                        profileService
                                            .getProfile(
                                                article.getAuthor().getUsername(), currentUserId)
                                            .map(
                                                profileData ->
                                                    configArticleData(
                                                        article,
                                                        profileData,
                                                        tags,
                                                        isFavorited,
                                                        favoritesCount)))));
  }

  private ArticleData configArticleData(
      Article article,
      ProfileData authorProfile,
      List<Tag> tags,
      boolean isFavorited,
      Long favoritesCount) {
    ArticleData articleData = new ArticleData(article, authorProfile);
    articleData.setTagList(tags.stream().map(Tag::getName).collect(Collectors.toList()));
    articleData.setFavorited(isFavorited);
    articleData.setFavoritesCount(favoritesCount);
    return articleData;
  }

  @Override
  public Single<ArticleData> create(String currentUserId, NewArticle newArticle) {
    modelValidator.validate(newArticle);
    Article article = createFromNewArticle(currentUserId, newArticle);
    return validAndConfigSlug(article)
        .andThen(configTags(article, newArticle.getTags()))
        .andThen(articleRepository.store(article))
        .flatMap(this::configAuthor)
        .flatMap(persistedArticle -> toArticleData(persistedArticle, currentUserId));
  }

  private Single<Article> configAuthor(Article article) {
    return userService
        .findById(article.getAuthor().getId())
        .map(
            author -> {
              article.setAuthor(author);
              return article;
            });
  }

  @Override
  public Single<Long> totalUserArticlesFollowed(String currentUserId) {
    return usersFollowedRepository.totalUserArticlesFollowed(currentUserId);
  }

  @Override
  public Single<Long> totalArticles(
      List<String> tags, List<String> authors, List<String> favorited) {
    return articleRepository.totalArticles(tags, authors, favorited);
  }

  @Override
  public Single<ArticleData> findBySlug(String slug, String currentUserId) {
    return findBySlug(slug)
        .flatMap(this::configAuthor)
        .flatMap(article -> toArticleData(article, currentUserId));
  }

  public Single<Article> findBySlug(String slug) {
    return articleRepository
        .findBySlug(slug)
        .map(article -> article.orElseThrow(ArticleNotFoundException::new));
  }

  @Override
  public Single<ArticlesData> findArticles(
      String currentUserId,
      int offset,
      int limit,
      List<String> tags,
      List<String> authors,
      List<String> favorited) {
    return articleRepository
        .findArticles(offset, getLimit(limit), tags, authors, favorited)
        .flattenAsFlowable(articles -> articles)
        .flatMapSingle(article -> toArticleData(article, currentUserId))
        .sorted(articleComparator())
        .toList()
        .flatMap(
            articles ->
                totalArticles(tags, authors, favorited)
                    .map(articlesCount -> new ArticlesData(articles, articlesCount)));
  }

  public Single<Boolean> isFavorited(String articleId, String currentUserId) {
    return favoritesRepository
        .countByArticleIdAndUserId(articleId, currentUserId)
        .map(this::isCountResultGreaterThanZero);
  }

  public Single<Long> favoritesCount(String articleId) {
    return favoritesRepository.countByArticleId(articleId);
  }

  private Completable validAndConfigSlug(Article article) {
    return isSlugAlreadyExists(article.getSlug())
        .flatMapCompletable(
            isSlugAlreadyExists -> {
              if (isSlugAlreadyExists) {
                article.setSlug(article.getSlug() + "_" + UUID.randomUUID().toString());
              }
              return Completable.complete();
            });
  }

  private Completable configTags(Article article, List<String> tags) {
    return Flowable.fromIterable(tags)
        .flatMapSingle(tag -> tagService.findTagByName(tag))
        .flatMapCompletable(
            tag -> {
              article.getTags().add(tag.orElseThrow(TagNotFoundException::new));
              return Completable.complete();
            });
  }

  private Single<Boolean> isSlugAlreadyExists(String slug) {
    return articleRepository.countBySlug(slug).map(this::isCountResultGreaterThanZero);
  }

  private Comparator<ArticleData> articleComparator() {
    return (article1, article2) -> {
      if (article1.getCreatedAt().isBefore(article2.getCreatedAt())) {
        return 1;
      }
      if (article1.getCreatedAt().isAfter(article2.getCreatedAt())) {
        return -1;
      }
      return 0;
    };
  }

  private Article createFromNewArticle(String currentUserId, NewArticle newArticle) {
    Article article = new Article();
    article.setTitle(newArticle.getTitle());
    article.setSlug(slugProvider.slugify(article.getTitle()));
    article.setDescription(newArticle.getDescription());
    article.setBody(newArticle.getBody());
    article.setAuthor(new User(currentUserId));
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    return article;
  }

  private int getLimit(int limit) {
    return limit > 0 ? limit : DEFAULT_LIMIT;
  }
}
