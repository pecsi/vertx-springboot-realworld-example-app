package com.example.realworld.domain.user.exception;

import java.util.LinkedList;
import java.util.List;

public class BusinessException extends RuntimeException {

  private List<String> messages;

  public BusinessException() {
    this.messages = new LinkedList<>();
  }

  public BusinessException(String message) {
    this();
    this.messages.add(message);
  }

  public BusinessException(List<String> messages) {
    this.messages = messages;
  }

  public boolean haveMessages() {
    return this.messages != null && this.getMessages().size() > 0;
  }

  public List<String> getMessages() {
    return messages;
  }

  public void setMessages(List<String> messages) {
    this.messages = messages;
  }
}
