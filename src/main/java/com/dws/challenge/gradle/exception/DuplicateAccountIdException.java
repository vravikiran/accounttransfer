package com.dws.challenge.gradle.exception;

public class DuplicateAccountIdException extends RuntimeException {

  private static final long serialVersionUID = 1L;

public DuplicateAccountIdException(String message) {
    super(message);
  }
}
