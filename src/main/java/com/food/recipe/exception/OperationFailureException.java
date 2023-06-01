package com.food.recipe.exception;

public class OperationFailureException extends RuntimeException {

    public OperationFailureException(String message) {
        super(message);
    }
}
