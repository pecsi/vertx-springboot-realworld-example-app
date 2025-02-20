package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.ArticleRepository;
import com.example.realworld.domain.article.model.FavoritesRepository;
import com.example.realworld.domain.tag.model.ArticlesTagsRepository;
import com.example.realworld.infrastructure.persistence.statement.ArticleStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ArticleRepositoryJDBC extends JDBCRepository implements ArticleRepository {

  private JDBCClient jdbcClient;
  private ArticleStatements articleStatements;
  private ArticlesTagsRepository articlesTagsRepository;
  private FavoritesRepository favoritesRepository;

  public ArticleRepositoryJDBC(
      JDBCClient jdbcClient,
      ArticleStatements articleStatements,
      ArticlesTagsRepository articlesTagsRepository,
      FavoritesRepository favoritesRepository) {
    this.jdbcClient = jdbcClient;
    this.articleStatements = articleStatements;
    this.articlesTagsRepository = articlesTagsRepository;
    this.favoritesRepository = favoritesRepository;
  }

  @Override
  public Single<Long> countBySlug(String slug) {
    Statement<JsonArray> countBySlugStatement = articleStatements.countBy("slug", slug);
    return jdbcClient
        .rxQueryWithParams(countBySlugStatement.sql(), countBySlugStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Long> countBySlug(String slug, String excludeArticleId) {
    Statement<JsonArray> countBySlugStatement =
        articleStatements.countBy("slug", slug, excludeArticleId);
    return jdbcClient
        .rxQueryWithParams(countBySlugStatement.sql(), countBySlugStatement.params())
        .map(this::getCountFromResultSet);
  }

  @Override
  public Single<Article> store(Article article) {
    article.setId(UUID.randomUUID().toString());
    Statement<JsonArray> storeArticleStatement = articleStatements.store(article);
    return jdbcClient
        .rxUpdateWithParams(storeArticleStatement.sql(), storeArticleStatement.params())
        .flatMapCompletable(
            updateResult ->
                Flowable.fromIterable(article.getTags())
                    .flatMapCompletable(tag -> articlesTagsRepository.tagArticle(tag, article)))
        .toSingleDefault(article);
  }

  @Override
  public Single<List<Article>> findArticles(
      int offset, int limit, List<String> tags, List<String> authors, List<String> favorited) {
    Statement<JsonArray> findArticlesStatement =
        articleStatements.findArticles(offset, limit, tags, authors, favorited);
    return jdbcClient
        .rxQueryWithParams(findArticlesStatement.sql(), findArticlesStatement.params())
        .map(ParserUtils::toArticleList);
  }

  @Override
  public Single<Long> totalArticles(
      List<String> tags, List<String> authors, List<String> favorited) {
    Statement<JsonArray> totalArticlesStatement =
        articleStatements.totalArticles(tags, authors, favorited);
    return jdbcClient
        .rxQueryWithParams(totalArticlesStatement.sql(), totalArticlesStatement.params())
        .map(resultSet -> resultSet.getRows().get(0).getLong("COUNT(DISTINCT ARTICLES.ID)"));
  }

  @Override
  public Single<Optional<Article>> findBySlug(String slug) {
    Statement<JsonArray> findBySlugStatement = articleStatements.findBySlug(slug);
    return jdbcClient
        .rxQueryWithParams(findBySlugStatement.sql(), findBySlugStatement.params())
        .map(ParserUtils::toArticleOptional);
  }

  @Override
  public Single<Article> update(Article article) {
    Statement<JsonArray> updateStatement = articleStatements.update(article);
    return jdbcClient
        .rxUpdateWithParams(updateStatement.sql(), updateStatement.params())
        .map(updateResult -> article);
  }

  @Override
  public Completable deleteByArticleIdAndAuthorId(String articleId, String authorId) {
    Statement<JsonArray> deleteByArticleIdAndAuthorId =
        articleStatements.deleteByArticleIdAndAuthorId(articleId, authorId);
    return articlesTagsRepository
        .deleteByArticle(articleId)
        .andThen(favoritesRepository.deleteByArticle(articleId))
        .andThen(
            jdbcClient
                .rxUpdateWithParams(
                    deleteByArticleIdAndAuthorId.sql(), deleteByArticleIdAndAuthorId.params())
                .flatMapCompletable(updateResult -> Completable.complete()));
  }

  @Override
  public Single<Optional<Article>> findBySlugAndAuthorId(String slug, String authorId) {
    Statement<JsonArray> findBySlugAndAuthorIdStatement =
        articleStatements.findBySlugAndAuthorId(slug, authorId);
    return jdbcClient
        .rxQueryWithParams(
            findBySlugAndAuthorIdStatement.sql(), findBySlugAndAuthorIdStatement.params())
        .map(ParserUtils::toArticleOptional);
  }
}
