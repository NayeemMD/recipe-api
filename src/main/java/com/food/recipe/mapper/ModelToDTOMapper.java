package com.food.recipe.mapper;

import com.food.recipe.dto.v1.request.RecipeRequest;
import com.food.recipe.dto.v1.request.RecipeSearchRequest;
import com.food.recipe.dto.v1.response.PageRecipeDTO;
import com.food.recipe.dto.v1.response.RecipeDTO;
import com.food.recipe.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelToDTOMapper {

    public static Recipe map(RecipeRequest request) {
       return Recipe.builder()
               .withName(request.getName())
               .withType(request.getType())
               .withServings(request.getServings())
               .withIngredients(request.getIngredients().stream()
                       .map(name -> Ingredient.builder()
                               .withName(name)
                              .build())
                       .toList())
               .withAuthor(map(request.getAuthor()))
               .withInstructions(request.getInstructions())
               .build();
    }

    public static Author map(com.food.recipe.dto.v1.request.Author request) {
        return Author.builder()
                .withEmail(request.getEmail())
                .withName(request.getName())
                .build();
    }

    public static RecipeDTO map(Recipe recipe) {
        return RecipeDTO.builder()
                .withId(recipe.getId())
                .withName(recipe.getName())
                .withType(recipe.getType())
                .withServings(recipe.getServings())
                .withInstructions(recipe.getInstructions())
                .withAuthor(com.food.recipe.dto.v1.response.Author.builder()
                        .name(recipe.getAuthor().getName())
                        .build())
                .withIngredients(recipe.getIngredients().stream()
                        .map(Ingredient::getName)
                        .toList())
                .build();

    }

    public static PageRecipeDTO map(Page<Recipe> recipePage) {
        return PageRecipeDTO.builder()
                .withPageNumber(recipePage.getNumber())
                .withPageSize(recipePage.getSize())
                .withTotalPages(recipePage.getTotalPages())
                .withTotalItems(recipePage.getTotalElements())
                .withRecipes( recipePage.getContent().stream()
                        .map(ModelToDTOMapper::map)
                        .toList())
                .build();
    }

    public static List<RecipeDTO> map(List<Recipe> recipes) {
        return recipes.stream()
                .map(ModelToDTOMapper::map)
                .toList();
    }

    public static RecipeSearch map(RecipeSearchRequest request) {
        return RecipeSearch.builder()
                .withSearchCriteriaList(request.getQueries().stream()
                            .map(searchQuery -> new SearchCriteria(searchQuery.getFieldName(),
                                    searchQuery.getValue(), searchQuery.getOperator())).toList())
                .withOperator(request.getOperator() == null ? SearchOperator.OR : request.getOperator())
                .build();
    }

}
