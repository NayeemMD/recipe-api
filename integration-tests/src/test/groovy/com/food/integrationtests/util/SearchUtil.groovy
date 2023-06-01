package com.food.integrationtests.util

import com.food.integrationtests.v1.request.QueryFieldName
import com.food.integrationtests.v1.request.QueryOperator
import com.food.integrationtests.v1.request.RecipeSearchRequest
import com.food.integrationtests.v1.request.RecipeType
import com.food.integrationtests.v1.request.SearchOperator
import com.food.integrationtests.v1.request.SearchQuery


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


    static RecipeSearchRequest prepareSearchRequestWithIngredientNameAndRecipeName(String ingredient, String recipe) {
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
        input.operator = SearchOperator.AND
        input
    }

    static RecipeSearchRequest prepareSearchRequestWithInstructionAndRecipeName(String instruction, String recipeName) {
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
        input.operator = SearchOperator.AND
        input

    }

    static RecipeSearchRequest prepareSearchRequestWithType(RecipeType recipeType,
                                                            String recipeName) {
        SearchQuery query = new SearchQuery()
        query.fieldName = QueryFieldName.TYPE
        query.value = recipeType
        query.operator = QueryOperator.EQUALS

        SearchQuery query2 = new SearchQuery()
        query2.fieldName = QueryFieldName.NAME
        query2.value = recipeName
        query2.operator = QueryOperator.EQUALS

        RecipeSearchRequest input = new RecipeSearchRequest()
        input.queries = List.of(query, query2)
        input.operator = SearchOperator.AND
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
