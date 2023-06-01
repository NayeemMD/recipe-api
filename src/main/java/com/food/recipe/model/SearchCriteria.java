package com.food.recipe.model;

import java.io.Serial;
import java.io.Serializable;

public record SearchCriteria(QueryFieldName property, Object value, QueryOperator operator) implements Serializable  {
    @Serial
    private static final long serialVersionUID = 3L;
}
