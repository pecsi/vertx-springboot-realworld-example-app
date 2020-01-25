package com.example.realworld.application;

import com.example.realworld.domain.tag.exception.TagAlreadyExistsException;
import com.example.realworld.domain.tag.model.ArticlesTagsRepository;
import com.example.realworld.domain.tag.model.Tag;
import com.example.realworld.domain.tag.model.TagRepository;
import com.example.realworld.domain.tag.service.TagService;
import com.example.realworld.domain.user.model.ModelValidator;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;
import java.util.Optional;

public class TagServiceImpl extends ApplicationService implements TagService {

  private TagRepository tagRepository;
  private ArticlesTagsRepository articlesTagsRepository;
  private ModelValidator modelValidator;

  public TagServiceImpl(
      TagRepository tagRepository,
      ArticlesTagsRepository articlesTagsRepository,
      ModelValidator modelValidator) {
    this.tagRepository = tagRepository;
    this.articlesTagsRepository = articlesTagsRepository;
    this.modelValidator = modelValidator;
  }

  @Override
  public Single<Tag> create(String tagName) {
    Tag tag = new Tag();
    tag.setName(tagName);
    return validTagName(tag.getName()).andThen(tagRepository.store(tag));
  }

  @Override
  public Single<List<Tag>> findTagsByArticle(String articleId) {
    return articlesTagsRepository.findTagsByArticle(articleId);
  }

  @Override
  public Single<Optional<Tag>> findTagByName(String name) {
    return tagRepository.findByName(name);
  }

  @Override
  public Single<List<Tag>> findAll() {
    return tagRepository.findAll();
  }

  private Completable validTagName(String tagName) {
    return isTagNameExists(tagName)
        .flatMapCompletable(
            isTagNameExists -> {
              if (isTagNameExists) {
                throw new TagAlreadyExistsException();
              }
              return Completable.complete();
            });
  }

  private Single<Boolean> isTagNameExists(String tagName) {
    return tagRepository.countByName(tagName).map(this::isCountResultGreaterThanZero);
  }
}
