package com.food.integrationtests.util

import com.food.integrationtests.v1.request.RecipeRequest
import com.food.integrationtests.v1.response.RecipeDTO


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

}
