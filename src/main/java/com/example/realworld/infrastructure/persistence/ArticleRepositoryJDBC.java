package com.example.realworld.infrastructure.persistence;

import com.example.realworld.domain.article.model.ArticleRepository;
import com.example.realworld.infrastructure.persistence.statement.ArticleStatements;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepositoryJDBC implements ArticleRepository {

  private ArticleStatements articleStatements;

  public ArticleRepositoryJDBC(ArticleStatements articleStatements) {
    this.articleStatements = articleStatements;
  }
}
