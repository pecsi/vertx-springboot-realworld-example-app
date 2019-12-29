package com.example.realworld.infrastructure.slug;

import com.example.realworld.domain.article.model.SlugProvider;
import com.github.slugify.Slugify;
import org.springframework.stereotype.Component;

@Component
public class SlugifyProvider implements SlugProvider {

  private Slugify slugify;

  private SlugifyProvider(Slugify slugify) {
    this.slugify = slugify;
  }

  @Override
  public String slugify(String text) {
    return slugify.slugify(text);
  }
}
