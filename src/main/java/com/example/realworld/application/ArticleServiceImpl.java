package com.example.realworld.application;

import com.example.realworld.domain.article.exception.SlugAlreadyExistsException;
import com.example.realworld.domain.article.model.*;
import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.domain.profile.model.FollowedUsersRepository;
import com.example.realworld.domain.profile.model.Profile;
import com.example.realworld.domain.profile.service.ProfileService;
import com.example.realworld.domain.tag.exception.TagNotFoundException;
import com.example.realworld.domain.tag.model.NewTag;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.tag.service.TagService;
import com.example.realworld.domain.user.model.ModelValidator;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ArticleServiceImpl extends ApplicationService implements ArticleService {

  private static final int DEFAULT_LIMIT = 20;

  private ArticleRepository articleRepository;
  private FollowedUsersRepository followedUsersRepository;
  private FavoritesRepository favoritesRepository;
  private SlugProvider slugProvider;
  private ModelValidator modelValidator;
  private ProfileService profileService;
  private TagService tagService;

  public ArticleServiceImpl(
      ArticleRepository articleRepository,
      FollowedUsersRepository followedUsersRepository,
      FavoritesRepository favoritesRepository,
      SlugProvider slugProvider,
      ModelValidator modelValidator,
      ProfileService profileService,
      TagService tagService) {
    this.articleRepository = articleRepository;
    this.followedUsersRepository = followedUsersRepository;
    this.favoritesRepository = favoritesRepository;
    this.slugProvider = slugProvider;
    this.modelValidator = modelValidator;
    this.profileService = profileService;
    this.tagService = tagService;
  }

  @Override
  public Single<List<Article>> findRecentArticles(String currentUserId, int offset, int limit) {
    return followedUsersRepository
        .findRecentArticles(currentUserId, offset, getLimit(limit))
        .flattenAsFlowable(articles -> articles)
        .flatMapSingle(
            article ->
                isFavorited(article.getId(), currentUserId)
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
                                                            article.getAuthor().getUsername(),
                                                            currentUserId)
                                                        .map(
                                                            profile ->
                                                                completeArticle(
                                                                    article,
                                                                    profile,
                                                                    tags,
                                                                    isFavorited,
                                                                    favoritesCount))))))
        .sorted(articleComparator())
        .toList();
  }

  private Comparator<Article> articleComparator() {
    return (article1, article2) -> {
      if (article1.getCreatedAt().isBefore(article2.getCreatedAt())) {
        return -1;
      }
      if (article1.getCreatedAt().isAfter(article2.getCreatedAt())) {
        return 1;
      }
      return 0;
    };
  }

  private Article completeArticle(
      Article target, Profile profile, List<Tag> tags, boolean isFavorited, Long favoritesCount) {
    target.setAuthor(profile);
    target.setTags(tags);
    target.setFavorited(isFavorited);
    target.setFavoritesCount(favoritesCount);
    return target;
  }

  @Override
  public Single<Article> create(NewArticle newArticle) {
    modelValidator.validate(newArticle);
    Article article = createFromNewArticle(newArticle);
    return validSlug(article.getSlug())
        .andThen(configTags(article, newArticle.getTags()))
        .andThen(articleRepository.store(article, newArticle.getAuthor()));
  }

  @Override
  public Single<Long> totalUserArticlesFollowed(String currentUserId) {
    return followedUsersRepository.totalUserArticlesFollowed(currentUserId);
  }

  @Override
  public Single<List<Article>> findArticles(
      String currentUserId,
      int offset,
      int limit,
      List<String> tags,
      List<String> authors,
      List<String> favorited) {

    return articleRepository
        .findArticles(offset, getLimit(limit), tags, authors, favorited)
        .flattenAsFlowable(articles -> articles)
        .flatMapSingle(
            article ->
                isFavorited(article.getId(), currentUserId)
                    .flatMap(
                        isFavorited ->
                            favoritesCount(article.getId())
                                .flatMap(
                                    favoritesCount ->
                                        tagService
                                            .findTagsByArticle(article.getId())
                                            .flatMap(
                                                persistedTags ->
                                                    profileService
                                                        .getProfile(
                                                            article.getAuthor().getUsername(),
                                                            currentUserId)
                                                        .map(
                                                            profile ->
                                                                completeArticle(
                                                                    article,
                                                                    profile,
                                                                    persistedTags,
                                                                    isFavorited,
                                                                    favoritesCount))))))
        .sorted(articleComparator())
        .toList();

  }

  public Single<Boolean> isFavorited(String articleId, String currentUserId) {
    return favoritesRepository
        .countByArticleIdAndUserId(articleId, currentUserId)
        .map(this::isCountResultGreaterThanZero);
  }

  public Single<Long> favoritesCount(String articleId) {
    return favoritesRepository.countByArticleId(articleId);
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
    article.setAuthor(new Profile((newArticle.getAuthor())));
    LocalDateTime now = LocalDateTime.now();
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    return article;
  }

  private int getLimit(int limit) {
    return limit > 0 ? limit : DEFAULT_LIMIT;
  }
}
