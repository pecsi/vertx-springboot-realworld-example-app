package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.Article;
import com.example.realworld.domain.tag.model.ArticlesTagsRepository;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.infrastructure.persistence.statement.ArticlesTagsStatements;
import com.example.realworld.infrastructure.persistence.statement.Statement;
import com.example.realworld.infrastructure.persistence.utils.ParserUtils;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticlesTagsRepositoryJDBC extends JDBCRepository implements ArticlesTagsRepository {

  private JDBCClient jdbcClient;
  private ArticlesTagsStatements articlesTagsStatements;

  public ArticlesTagsRepositoryJDBC(
      JDBCClient jdbcClient, ArticlesTagsStatements articlesTagsStatements) {
    this.jdbcClient = jdbcClient;
    this.articlesTagsStatements = articlesTagsStatements;
  }

  @Override
  public Single<List<Tag>> findTagsByArticle(String articleId) {
    Statement<JsonArray> findTagsByArticleStatement =
        articlesTagsStatements.findTagsByArticle(articleId);
    return jdbcClient
        .rxQueryWithParams(findTagsByArticleStatement.sql(), findTagsByArticleStatement.params())
        .map(ParserUtils::toTagList);
  }

  @Override
  public Completable tagArticle(Tag tag, Article article) {
    Statement<JsonArray> tagArticleStatement =
        articlesTagsStatements.store(tag.getId(), article.getId());
    return jdbcClient
        .rxUpdateWithParams(tagArticleStatement.sql(), tagArticleStatement.params())
        .flatMapCompletable(updateResult -> Completable.complete());
  }
}
