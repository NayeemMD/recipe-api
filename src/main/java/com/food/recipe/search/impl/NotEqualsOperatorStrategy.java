package com.food.recipe.search.impl;

import com.food.recipe.search.SearchOperatorStrategy;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

public class NotEqualsOperatorStrategy<T> implements SearchOperatorStrategy<T> {
    @Override
    public Specification<T> buildSpecification(Path<?> field, Object value) {
        return (root, query, builder) -> builder.notEqual(builder.lower(field.as(String.class)), value);
    }
}

