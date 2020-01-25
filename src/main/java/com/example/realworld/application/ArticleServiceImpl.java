package com.example.realworld.application;

import com.example.realworld.application.data.ArticleData;
import com.example.realworld.application.data.ArticlesData;
import com.example.realworld.application.data.CommentData;
import com.example.realworld.application.data.ProfileData;
import com.example.realworld.domain.article.exception.ArticleAlreadyFavoritedException;
import com.example.realworld.domain.article.exception.ArticleAlreadyUnfavoritedException;
import com.example.realworld.domain.article.exception.ArticleNotFoundException;
import com.example.realworld.domain.article.model.*;
import com.example.realworld.domain.article.service.ArticleService;
import com.example.realworld.domain.profile.model.UsersFollowedRepository;
import com.example.realworld.domain.profile.service.ProfileService;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArticleServiceImpl extends ApplicationService implements ArticleService {

  private static final int DEFAULT_LIMIT = 20;

  private ArticleRepository articleRepository;
  private UsersFollowedRepository usersFollowedRepository;
  private FavoritesRepository favoritesRepository;
  private CommentRepository commentRepository;
  private SlugProvider slugProvider;
  private ModelValidator modelValidator;
  private ProfileService profileService;
  private TagService tagService;
  private UserService userService;

  public ArticleServiceImpl(
      ArticleRepository articleRepository,
      UsersFollowedRepository usersFollowedRepository,
      FavoritesRepository favoritesRepository,
      CommentRepository commentRepository,
      SlugProvider slugProvider,
      ModelValidator modelValidator,
      ProfileService profileService,
      TagService tagService,
      UserService userService) {
    this.articleRepository = articleRepository;
    this.usersFollowedRepository = usersFollowedRepository;
    this.favoritesRepository = favoritesRepository;
    this.commentRepository = commentRepository;
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

  @Override
  public Single<ArticleData> updateBySlug(
      String slug, String currentUserId, UpdateArticle updateArticle) {
    modelValidator.validate(updateArticle);
    return findBySlugAndAuthorId(slug, currentUserId)
        .flatMap(
            article ->
                configUpdateFields(article, updateArticle)
                    .flatMapCompletable(this::validAndConfigSlug)
                    .andThen(articleRepository.update(article))
                    .flatMap(this::configAuthor)
                    .flatMap(updatedArticle -> toArticleData(updatedArticle, currentUserId)));
  }

  @Override
  public Completable deleteArticleBySlugAndAuthorId(String slug, String currentUserId) {
    return findBySlugAndAuthorId(slug, currentUserId)
        .flatMapCompletable(
            article ->
                articleRepository.deleteByArticleIdAndAuthorId(article.getId(), currentUserId));
  }

  @Override
  public Single<CommentData> createCommentBySlug(
      String slug, String currentUserId, String commentBody) {
    return findBySlug(slug)
        .flatMap(
            article ->
                userService
                    .findById(currentUserId)
                    .flatMap(
                        commentAuthor ->
                            createComment(article, commentAuthor, commentBody)
                                .flatMap(
                                    comment ->
                                        commentRepository
                                            .store(comment)
                                            .flatMap(
                                                persistedComment ->
                                                    profileService
                                                        .getProfileById(commentAuthor.getId())
                                                        .map(
                                                            authorProfile ->
                                                                new CommentData(
                                                                    persistedComment,
                                                                    authorProfile))))));
  }

  @Override
  public Completable deleteCommentByIdAndAuthorId(String commentId, String currentUserId) {
    return commentRepository.deleteByCommentIdAndAuthorId(commentId, currentUserId);
  }

  @Override
  public Single<List<CommentData>> findCommentsBySlug(String slug, String currentUserId) {
    return findBySlug(slug)
        .flatMap(
            article ->
                commentRepository
                    .findCommentsByArticleId(article.getId())
                    .flattenAsFlowable(comments -> comments)
                    .flatMapSingle(
                        comment ->
                            profileService
                                .getProfile(comment.getAuthor().getUsername(), currentUserId)
                                .map(profileData -> new CommentData(comment, profileData)))
                    .toList());
  }

  @Override
  public Single<ArticleData> favoriteArticle(String slug, String currentUserId) {
    return findBySlug(slug)
        .flatMapCompletable(
            article ->
                validFavorited(article.getId(), currentUserId)
                    .andThen(favoritesRepository.store(article.getId(), currentUserId)))
        .andThen(findBySlug(slug, currentUserId));
  }

  @Override
  public Single<ArticleData> unfavoriteArticle(String slug, String currentUserId) {
    return findBySlug(slug)
        .flatMapCompletable(
            article ->
                validUnfavorited(article.getId(), currentUserId)
                    .andThen(
                        favoritesRepository.deleteByArticleAndAuthor(
                            article.getId(), currentUserId)))
        .andThen(findBySlug(slug, currentUserId));
  }

  private Completable validUnfavorited(String articleId, String currentUserId) {
    return isFavorited(articleId, currentUserId)
        .flatMapCompletable(
            isFavorited -> {
              if (!isFavorited) {
                throw new ArticleAlreadyUnfavoritedException();
              }
              return Completable.complete();
            });
  }

  private Completable validFavorited(String articleId, String currentUserId) {
    return isFavorited(articleId, currentUserId)
        .flatMapCompletable(
            isFavorited -> {
              if (isFavorited) {
                throw new ArticleAlreadyFavoritedException();
              }
              return Completable.complete();
            });
  }

  private Single<Comment> createComment(Article article, User author, String commentBody) {
    Comment comment = new Comment();
    comment.setArticle(article);
    comment.setAuthor(author);
    comment.setBody(commentBody);
    LocalDateTime now = LocalDateTime.now();
    comment.setCreatedAt(now);
    comment.setUpdatedAt(now);
    return Single.just(comment);
  }

  private Single<Article> configUpdateFields(Article article, UpdateArticle updateArticle) {
    article.setTitle(updateArticle.getTitle());
    article.setDescription(updateArticle.getDescription());
    article.setBody(updateArticle.getBody());
    return Single.just(article);
  }

  public Single<Article> findBySlug(String slug) {
    return articleRepository
        .findBySlug(slug)
        .map(article -> article.orElseThrow(ArticleNotFoundException::new));
  }

  public Single<Article> findBySlugAndAuthorId(String slug, String authorId) {
    return articleRepository
        .findBySlugAndAuthorId(slug, authorId)
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
    return Single.just(article.getId() != null)
        .flatMap(
            isArticleIdPresent -> {
              if (isArticleIdPresent) {
                return isSlugAlreadyExists(article.getSlug(), article.getId());
              } else {
                return isSlugAlreadyExists(article.getSlug());
              }
            })
        .flatMapCompletable(
            isSlugAlreadyExists -> configNewSlugIfExists(article, isSlugAlreadyExists));
  }

  private Completable configNewSlugIfExists(Article article, boolean isSlugAlreadyExists) {
    if (isSlugAlreadyExists) {
      article.setSlug(article.getSlug() + "_" + UUID.randomUUID().toString());
    }
    return Completable.complete();
  }

  private Completable configTags(Article article, List<String> tags) {
    return Flowable.fromIterable(tags)
        .flatMapCompletable(
            tagName ->
                tagService
                    .findTagByName(tagName)
                    .flatMap(persistedTag -> createTagIfNotExists(persistedTag, tagName))
                    .flatMapCompletable(
                        tag -> {
                          article.getTags().add(tag);
                          return Completable.complete();
                        }));
  }

  private Single<Tag> createTagIfNotExists(Optional<Tag> tagOptional, String tagName) {
    if (tagOptional.isPresent()) {
      return Single.just(tagOptional.get());
    } else {
      return tagService.create(tagName);
    }
  }

  private Single<Boolean> isSlugAlreadyExists(String slug) {
    return articleRepository.countBySlug(slug).map(this::isCountResultGreaterThanZero);
  }

  private Single<Boolean> isSlugAlreadyExists(String slug, String excludeArticleId) {
    return articleRepository
        .countBySlug(slug, excludeArticleId)
        .map(this::isCountResultGreaterThanZero);
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
