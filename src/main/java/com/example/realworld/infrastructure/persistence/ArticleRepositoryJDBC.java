package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.article.model.ArticleRepository;
import com.example.realworld.domain.tag.model.ArticlesTagsRepository;
import com.example.realworld.infrastructure.persistence.statement.ArticleStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

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

    //    return jdbcClient
    //        .rxUpdateWithParams(storeArticleStatement.sql(), storeArticleStatement.params())
    //        .map(updateResult -> article);
  }
}
