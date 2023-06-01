package com.food.recipe.search;

import com.food.recipe.model.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SpecificationBuilder<T> {

    @SuppressWarnings("unchecked")
    public Specification<T> build(List<SearchCriteria> searchCriteriaList) {
        return searchCriteriaList.stream()
                .map(criteria -> (Specification<T>) new SearchCriteriaSpecification<>(criteria))
                .reduce(Specification.where(null), Specification::and);
    }
}

