package main.exception;

import javax.persistence.PersistenceException;

public class IllegalValueException extends PersistenceException {

  public IllegalValueException() {
  }

  public IllegalValueException(String message) {
    super(message);
  }
}
