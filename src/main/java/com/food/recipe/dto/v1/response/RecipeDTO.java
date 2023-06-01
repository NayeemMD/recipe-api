package com.food.recipe.dto.v1.response;


import com.food.recipe.model.RecipeType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Builder(setterPrefix = "with")
public class RecipeDTO {
    private Integer id;
    private String name;
    private RecipeType type;
    private Integer servings;
    private String instructions;
    private List<String> ingredients;
    private Author author;
}
