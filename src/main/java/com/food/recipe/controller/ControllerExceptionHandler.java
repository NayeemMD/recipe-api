package com.food.recipe.controller;

import com.food.recipe.exception.OperationFailureException;
import com.food.recipe.exception.RequestValidationException;
import com.food.recipe.dto.v1.error.APIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<APIError> badRequestHandler(RequestValidationException exception) {
        return prepareErrorResponseEntity(BAD_REQUEST, exception, List.of(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<APIError> internalExceptionHandler(Exception exception) {
        log.error("API error occurred:", exception);
        return prepareErrorResponseEntity(INTERNAL_SERVER_ERROR, exception, List.of("Unexpected error occurred"));
    }

    @ExceptionHandler
    public ResponseEntity<APIError> internalExceptionHandler(OperationFailureException exception) {
        return prepareErrorResponseEntity(INTERNAL_SERVER_ERROR, exception, List.of(exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<APIError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        return prepareErrorResponseEntity(BAD_REQUEST, ex, errors);
    }

    private static ResponseEntity<APIError> prepareErrorResponseEntity(HttpStatus status, Exception ex, List<String> errors) {
        APIError error = APIError.builder()
                .status(status.value())
                .errors(errors)
                .build();

        log.error("API error occurred: {}", error, ex);
        return ResponseEntity.badRequest().body(error);
    }
}
