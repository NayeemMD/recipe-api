package com.food.recipe.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity(name = "recipe_ingredient")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientEntity {
    @EmbeddedId
    private RecipeIngredientId id;

    @MapsId("ingredientId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientEntity ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    private RecipeEntity recipe;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeIngredientEntity that = (RecipeIngredientEntity) o;

        if (!id.equals(that.id)) return false;
        return ingredient.equals(that.ingredient);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + ingredient.hashCode();
        return result;
    }

    public RecipeIngredientEntity(IngredientEntity ingredient, RecipeEntity recipe) {
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.id = new RecipeIngredientId(recipe.getId(), ingredient.getId());
    }
}