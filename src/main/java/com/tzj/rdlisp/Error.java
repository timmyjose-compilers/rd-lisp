package com.tzj.rdlisp;

public class Error extends RuntimeException {
  private String message;

  public Error(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return message;
  }
}
