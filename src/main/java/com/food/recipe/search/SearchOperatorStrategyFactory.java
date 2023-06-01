package com.food.recipe.search;

import com.food.recipe.model.QueryOperator;
import com.food.recipe.search.impl.*;

import java.util.EnumMap;
import java.util.Map;

public class SearchOperatorStrategyFactory<T> {
    private final Map<QueryOperator, SearchOperatorStrategy<T>> strategyMap;

    private SearchOperatorStrategyFactory() {
        strategyMap = new EnumMap<>(QueryOperator.class);
        strategyMap.put(QueryOperator.EQUALS, new EqualsOperatorStrategy<>());
        strategyMap.put(QueryOperator.NOT_EQUALS, new NotEqualsOperatorStrategy<>());
        strategyMap.put(QueryOperator.CONTAINS, new ContainsOperatorStrategy<>());
        strategyMap.put(QueryOperator.NOT_CONTAINS, new NotContainsOperatorStrategy<>());
        strategyMap.put(QueryOperator.GREATER_THAN, new GreaterThanOperatorStrategy<>());
        strategyMap.put(QueryOperator.LESS_THAN, new LessThanOperatorStrategy<>());
    }

    public static <T> SearchOperatorStrategyFactory<T> getInstance() {
        return SearchOperatorStrategyFactoryHolder.getInstance();
    }

    private static class SearchOperatorStrategyFactoryHolder<T> {
        private static final SearchOperatorStrategyFactory<?> INSTANCE = new SearchOperatorStrategyFactory<>();

        @SuppressWarnings("unchecked")
        private static <T> SearchOperatorStrategyFactory<T> getInstance() {
            return (SearchOperatorStrategyFactory<T>) INSTANCE;
        }
    }

    // Get the strategy based on the operator type
    public SearchOperatorStrategy<T> getStrategy(QueryOperator operator) {
        return strategyMap.get(operator);
    }
}


