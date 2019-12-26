package com.example.realworld.infrastructure.validator;

import com.example.realworld.domain.user.exception.BusinessException;
import com.example.realworld.domain.user.model.ModelValidator;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ModelValidatorImpl implements ModelValidator {

  private Validator validator;

  public ModelValidatorImpl(Validator validator) {
    this.validator = validator;
  }

  @Override
  public <T> void validate(T model) {
    Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(model);

    List<String> errorMessages =
        constraintViolationSet.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());

    if (!errorMessages.isEmpty()) {
      throw new BusinessException(errorMessages);
    }
  }
}
