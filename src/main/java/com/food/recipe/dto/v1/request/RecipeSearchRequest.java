package com.food.recipe.dto.v1.request;

import com.food.recipe.model.SearchOperator;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeSearchRequest {

    @NotEmpty(message = "Field - 'queries' must not be empty")
    private List<SearchQuery> queries;

    private SearchOperator operator;
}
