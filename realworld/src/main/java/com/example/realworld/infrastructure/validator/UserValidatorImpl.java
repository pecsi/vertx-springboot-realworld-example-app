package com.example.realworld.infrastructure.validator;

import com.example.realworld.domain.user.exception.BusinessException;
import com.example.realworld.domain.user.model.NewUser;
import com.example.realworld.domain.user.model.UserValidator;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserValidatorImpl implements UserValidator {

  private Validator validator;

  public UserValidatorImpl(Validator validator) {
    this.validator = validator;
  }

  @Override
  public void validate(NewUser newUser) {
    Set<ConstraintViolation<NewUser>> constraintViolationSet = validator.validate(newUser);

    List<String> errorMessages =
        constraintViolationSet.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());

    if (!errorMessages.isEmpty()) {
      throw new BusinessException(errorMessages);
    }
  }
}
