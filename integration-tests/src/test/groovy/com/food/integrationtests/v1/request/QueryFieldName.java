package com.food.integrationtests.v1.request;

public enum QueryFieldName {
    NAME("name"),
    TYPE("type"),
    INSTRUCTIONS("instructions"),
    AUTHOR_NAME("name"),
    INGREDIENT_NAME("name"),
    SERVINGS("servings");
    private final String value;

    QueryFieldName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
