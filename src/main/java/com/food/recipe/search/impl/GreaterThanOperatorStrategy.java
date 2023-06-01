package com.food.recipe.search.impl;

import com.food.recipe.search.SearchOperatorStrategy;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;


public class GreaterThanOperatorStrategy<T> implements SearchOperatorStrategy<T> {
    @Override
    public Specification<T> buildSpecification(Path<?> field, Object value) {
            Number numericValue = Double.parseDouble((String) value);
            return (root, query, builder) -> builder.gt(field.as(Integer.class), numericValue);
    }
}
