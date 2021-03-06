package main.exception;

import lombok.extern.slf4j.Slf4j;
import main.response.ResponseResults;
import main.response.errors.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.persistence.EntityNotFoundException;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseResults handleValidationExceptions(MethodArgumentNotValidException ex) {
    HashMap<String, String> collections = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      collections.put(fieldName, errorMessage);
    });
    return new ResponseResults()
        .setResult(false)
        .setErrors(collections);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({EntityNotFoundException.class})
  public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
    log.error("Entity exception, bad request: {}", ex.getMessage());
    return ErrorResponse.builder()
        .message("Post / Comment not exist ")
        .build();
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(InternalAuthenticationServiceException.class)
  public ResponseResults handleInternalAuthenticationException(
      InternalAuthenticationServiceException e) {
    log.error(e.getMessage());
    return new ResponseResults()
        .setResult(false);
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public ResponseResults handleCheckAuthenticationException(
      AuthenticationCredentialsNotFoundException e) {
    log.error(e.getMessage());
    return new ResponseResults()
        .setResult(false);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseResults handleFileSizeLimitException(
      MaxUploadSizeExceededException e) {
    log.error(e.getMessage());
    return new ResponseResults()
        .setErrors(Map.of("file", "File too large"))
        .setResult(false);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalClassFormatException.class)
  public ErrorResponse handleIllegalImageFormatException(IllegalClassFormatException ex) {
    log.error("File not supported: {}", ex.getMessage());
    return ErrorResponse.builder()
        .message("Illegal file format")
        .build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(FileAlreadyExistsException.class)
  public ErrorResponse handleFileAlreadyExistsException(FileAlreadyExistsException ex) {
    log.error("File is already in the database: {}", ex.getMessage());
    return ErrorResponse.builder()
        .message("Photo already exists")
        .build();
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(StatusException.class)
  public ResponseResults handleStatusException(StatusException ex) {
    log.error("Settings not found in the database: {}", ex.getMessage());
    return new ResponseResults()
        .setResult(false);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalValueException.class)
  public ErrorResponse handleIllegalValueException(IllegalValueException ex) {
    log.error("Value cannot be like that: {}", ex.getMessage());
    return ErrorResponse.builder()
        .message("Only 1 or -1.")
        .build();
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler(AccessDeniedException.class)
  public void handleStatisticsAccessDeniedException(AccessDeniedException ex) {
    log.error("Access denied: {}", ex.getMessage());
  }

  @ResponseStatus(HttpStatus.OK)
  @ExceptionHandler(InvalidCaptchaException.class)
  public ResponseResults handleCaptchaException(InvalidCaptchaException ex) {
    log.error("invalid captcha: {}", ex.getMessage());
    return new ResponseResults()
        .setErrors(Map.of("captcha", ex.getMessage()))
        .setResult(false);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidAttributeException.class)
  public ResponseResults handleInvalidAttributeException(InvalidAttributeException ex) {
    log.error("{}: {}", ex.getMessage(), ex.getErrors());
    return new ResponseResults()
        .setResult(false)
        .setErrors(ex.getErrors());
  }

  @ExceptionHandler(StorageException.class)
  public ResponseEntity<?> handleStorageException(StorageException ex) {
    log.error(ex.getMessage());
    return ResponseEntity.status(500).build();
  }
}
