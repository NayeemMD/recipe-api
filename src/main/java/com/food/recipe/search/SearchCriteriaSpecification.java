package com.food.recipe.search;

import com.food.recipe.model.QueryFieldName;
import com.food.recipe.model.QueryOperator;
import com.food.recipe.model.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class SearchCriteriaSpecification<T> implements Specification<T> {
    private final SearchCriteria criteria;


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        QueryFieldName property = criteria.property();
        QueryOperator operator = criteria.operator();
        Object value = criteria.value();

        SearchOperatorStrategyFactory<T> strategyFactory = SearchOperatorStrategyFactory.getInstance();
        SearchOperatorStrategy<T> strategy = strategyFactory.getStrategy(operator);
        Specification<T> specification = strategy.buildSpecification(root.get(property.value()), value.toString().toLowerCase());
        return specification.toPredicate(root, query, builder);
    }
}

