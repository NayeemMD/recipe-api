package com.food.integrationtests.v1.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeSearchRequest {

    private List<SearchQuery> queries;

    private SearchOperator operator;
}
