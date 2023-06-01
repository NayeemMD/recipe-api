package com.food.recipe.util

import com.food.recipe.dto.v1.request.RecipeSearchRequest
import com.food.recipe.dto.v1.request.SearchQuery
import com.food.recipe.model.QueryFieldName
import com.food.recipe.model.QueryOperator
import com.food.recipe.model.RecipeType
import com.food.recipe.model.SearchOperator

class SearchUtil {

    static RecipeSearchRequest prepareSearchRequest(String s) {
        SearchQuery query = new SearchQuery()
        query.fieldName = QueryFieldName.NAME
        query.value = s
        query.operator = QueryOperator.EQUALS
        RecipeSearchRequest input = new RecipeSearchRequest()
        input.queries = List.of(query)

        input
    }

    static RecipeSearchRequest prepareSearchRequestWithIngredientName(String s) {
        SearchQuery query = new SearchQuery()
        query.fieldName = QueryFieldName.INGREDIENT_NAME
        query.value = s
        query.operator = QueryOperator.EQUALS
        RecipeSearchRequest input = new RecipeSearchRequest()
        input.queries = List.of(query)
        input
    }

    static RecipeSearchRequest prepareSearchRequestWithIngredientNameAndRecipeName(String ingredient, String recipe, SearchOperator searchOperator) {
        SearchQuery query = new SearchQuery()
        query.fieldName = QueryFieldName.INGREDIENT_NAME
        query.value = ingredient
        query.operator = QueryOperator.EQUALS

        SearchQuery query2 = new SearchQuery()
        query2.fieldName = QueryFieldName.NAME
        query2.value = recipe
        query2.operator = QueryOperator.EQUALS

        RecipeSearchRequest input = new RecipeSearchRequest()
        input.queries = List.of(query, query2)
        input.operator = searchOperator
        input
    }

    static RecipeSearchRequest prepareSearchRequestWithInstructionAndRecipeName(String instruction, String recipeName,
                                                                         SearchOperator searchOperator) {
        SearchQuery query = new SearchQuery()
        query.fieldName = QueryFieldName.INSTRUCTIONS
        query.value = instruction
        query.operator = QueryOperator.CONTAINS

        SearchQuery query2 = new SearchQuery()
        query2.fieldName = QueryFieldName.NAME
        query2.value = recipeName
        query2.operator = QueryOperator.EQUALS

        RecipeSearchRequest input = new RecipeSearchRequest()
        input.queries = List.of(query, query2)
        input.operator = searchOperator
        input

    }

    static RecipeSearchRequest prepareSearchRequestWithType(RecipeType recipeType, QueryOperator operator) {
        SearchQuery query = new SearchQuery()
        query.fieldName = QueryFieldName.TYPE
        query.value = recipeType
        query.operator = operator

        RecipeSearchRequest input = new RecipeSearchRequest()
        input.queries = List.of(query)
        input

    }

    static RecipeSearchRequest prepareSearchRequestWithServings(Integer servings, QueryOperator operator) {
        SearchQuery query = new SearchQuery()
        query.fieldName = QueryFieldName.SERVINGS
        query.value = servings
        query.operator = operator

        RecipeSearchRequest input = new RecipeSearchRequest()
        input.queries = List.of(query)
        input
    }
}
