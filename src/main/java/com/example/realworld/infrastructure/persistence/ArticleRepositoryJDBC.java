package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.ArticleRepository;
import com.example.realworld.domain.tag.model.ArticlesTagsRepository;
import com.example.realworld.infrastructure.persistence.statement.ArticleStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ArticleRepositoryJDBC extends JDBCRepository implements ArticleRepository {

  private JDBCClient jdbcClient;
  private ArticleStatements articleStatements;
  private ArticlesTagsRepository articlesTagsRepository;

  public ArticleRepositoryJDBC(
      JDBCClient jdbcClient,
      ArticleStatements articleStatements,
      ArticlesTagsRepository articlesTagsRepository) {
    this.jdbcClient = jdbcClient;
    this.articleStatements = articleStatements;
    this.articlesTagsRepository = articlesTagsRepository;
  }

  @Override
  public Single<Long> countBySlug(String slug) {
    Statement<JsonArray> countBySlugStatement = articleStatements.countBy("slug", slug);
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
}
