package com.food.recipe.util

import com.food.recipe.dto.v1.request.RecipeRequest
import com.food.recipe.dto.v1.response.RecipeDTO

class AssertUtil {

    static void validateResponseDTO(RecipeDTO response, RecipeRequest request) {
        assert response.name == request.name
        assert response.type == request.type
        assert response.servings == request.servings
        assert response.servings == request.servings
        assert response.instructions == request.instructions
        assert response.instructions == request.instructions
        assert response.ingredients == request.ingredients
        assert response.author.name == request.author.name
    }

    static void validateDatabaseEntries(RecipeDTO response, DataUtil dataUtil) {
        assert dataUtil.getRecipeById(response.getId()).isPresent()

        def recipeIngredientEntities = dataUtil.getRecipeIngredientIdsByRecipeId(response.getId())
        assert recipeIngredientEntities
        def ingredientEntities = recipeIngredientEntities.stream()
                .map { it -> it.ingredient }
                .map(it -> it.name)
                .sorted()
                .toList()


        assert ingredientEntities.size() == response.ingredients.size()
        assert ingredientEntities == response.ingredients.sort()
    }

    static void validateDatabaseEntriesForDelete(RecipeDTO response, DataUtil dataUtil) {
        assert dataUtil.getRecipeById(response.getId()).isEmpty()

        def recipeIngredientEntities = dataUtil.getRecipeIngredientIdsByRecipeId(response.getId())
        assert !recipeIngredientEntities
    }
}
