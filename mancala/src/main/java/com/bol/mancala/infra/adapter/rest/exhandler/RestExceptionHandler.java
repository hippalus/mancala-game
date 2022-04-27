package com.bol.mancala.infra.adapter.rest.exhandler;

import com.bol.mancala.game.exception.DataNotFoundException;
import com.bol.mancala.game.exception.MancalaGameException;
import com.bol.mancala.infra.adapter.rest.dto.response.ErrorResponse;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleException(final Exception exception) {
    log.error("An error occurred! Details: ", exception);
    return ResponseEntity.internalServerError().body(new ErrorResponse("500", exception.getMessage()));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleRequestPropertyBindingError(
      final WebExchangeBindException webExchangeBindException) {
    log.debug("Bad request!", webExchangeBindException);
    return createFieldErrorResponse(webExchangeBindException.getBindingResult());
  }

  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleBindException(final BindException bindException) {
    log.debug("Bad request!", bindException);
    return createFieldErrorResponse(bindException.getBindingResult());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleInvalidArgumentException(
      final MethodArgumentNotValidException methodArgumentNotValidException) {
    log.debug("Method argument not valid. Message: $methodArgumentNotValidException.message", methodArgumentNotValidException);
    return createFieldErrorResponse(methodArgumentNotValidException.getBindingResult());
  }

  @ExceptionHandler(MancalaGameException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<ErrorResponse> handleMancalaGameException(final MancalaGameException ex) {
    log.debug("Bad request!", ex);
    return ResponseEntity.unprocessableEntity()
        .body(new ErrorResponse("422", ex.getMessage()));
  }

  @ExceptionHandler(DataNotFoundException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<ErrorResponse> handleNotFoundException(final DataNotFoundException ex) {
    log.debug("Bad request!", ex);
    return ResponseEntity.unprocessableEntity()
        .body(new ErrorResponse("404", ex.getMessage()));
  }


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      final MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
    log.trace("MethodArgumentTypeMismatchException occurred", methodArgumentTypeMismatchException);
    return ResponseEntity.unprocessableEntity()
        .body(new ErrorResponse("422", methodArgumentTypeMismatchException.getMessage()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handle(final ConstraintViolationException exception) {
    final String errorMessage = exception.getConstraintViolations()
        .stream()
        .map(this::violationMessage)
        .collect(Collectors.joining(" && "));
    return ResponseEntity.badRequest().body(new ErrorResponse("400", errorMessage));
  }

  private String violationMessage(final ConstraintViolation<?> violation) {
    return violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage();
  }

  private ResponseEntity<ErrorResponse> createFieldErrorResponse(final Errors bindingResult) {
    final String errorMessage = bindingResult
        .getFieldErrors().stream()
        .map(FieldError::getField)
        .collect(Collectors.joining(" && "));

    log.debug("Exception occurred while request validation: {}", errorMessage);
    return ResponseEntity.badRequest().body(new ErrorResponse("400", "Wrong fields " + errorMessage));
  }


}