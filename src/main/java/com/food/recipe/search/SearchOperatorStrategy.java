package com.food.recipe.search;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;


public interface SearchOperatorStrategy<T> {
    Specification<T> buildSpecification(Path<?> field, Object value);
}
