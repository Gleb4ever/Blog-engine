package main.exception;

import java.util.Map;

public class InvalidAttributeException extends RuntimeException {

  private static final String INVALID_ATTR = "Invalid attribute";
  private final Map<String, String> errors;

  public InvalidAttributeException(Map<String, String> errors) {
    super(INVALID_ATTR);
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}
