package com.food.recipe.dto.v1.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder(setterPrefix = "with")
public class PageRecipeDTO {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalItems;
    private List<RecipeDTO> recipes;
}
