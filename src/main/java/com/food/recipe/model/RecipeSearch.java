package com.food.recipe.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(setterPrefix = "with")
public class RecipeSearch {
    private final List<SearchCriteria> searchCriteriaList;
    private final SearchOperator operator;
}
