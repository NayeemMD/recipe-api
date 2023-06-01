package com.food.recipe.dto.v1.request;

import com.food.recipe.model.QueryFieldName;
import com.food.recipe.model.QueryOperator;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchQuery {
    @NotEmpty(message = "Field -'fieldName' must not be empty")
    private QueryFieldName fieldName;

    @NotEmpty(message = "Field -'value' must not be empty")
    private Object value;

    @NotEmpty(message = "Field -'operator' must not be empty")
    private QueryOperator operator;
}
