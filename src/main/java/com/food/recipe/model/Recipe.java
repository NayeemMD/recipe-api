package com.food.recipe.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder(setterPrefix = "with")
@Getter
@EqualsAndHashCode
public class Recipe {
    @Setter
    private Integer id;
    private String name;
    private RecipeType type;
    private Integer servings;
    private String instructions;
    private List<Ingredient> ingredients;
    private Author author;
}
