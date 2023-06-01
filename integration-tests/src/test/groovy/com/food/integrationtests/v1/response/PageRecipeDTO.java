package com.food.integrationtests.v1.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PageRecipeDTO {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalItems;
    private List<RecipeDTO> recipes;
}
