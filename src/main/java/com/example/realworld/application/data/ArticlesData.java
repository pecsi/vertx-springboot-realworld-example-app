package com.example.realworld.application.data;

import java.util.List;

public class ArticlesData {
  private List<ArticleData> articles;
  private long articlesCount;

  public ArticlesData(List<ArticleData> articles, long articlesCount) {
    this.articles = articles;
    this.articlesCount = articlesCount;
  }

  public List<ArticleData> getArticles() {
    return articles;
  }

  public void setArticles(List<ArticleData> articles) {
    this.articles = articles;
  }

  public long getArticlesCount() {
    return articlesCount;
  }

  public void setArticlesCount(long articlesCount) {
    this.articlesCount = articlesCount;
  }
}
