package com.food.recipe.model;

import lombok.*;

@Builder(setterPrefix = "with")
@Getter
@EqualsAndHashCode
public class Ingredient {
    private String name;
}
