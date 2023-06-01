package com.food.recipe.util

import com.food.recipe.persistance.AuthorRepository
import com.food.recipe.persistance.IngredientRepository
import com.food.recipe.persistance.RecipeIngredientRepository
import com.food.recipe.persistance.RecipeRepository
import com.food.recipe.persistance.entity.AuthorEntity
import com.food.recipe.persistance.entity.IngredientEntity
import com.food.recipe.persistance.entity.RecipeEntity
import com.food.recipe.persistance.entity.RecipeIngredientEntity
import org.springframework.stereotype.Component

@Component
class DataUtil {
    private final RecipeRepository recipeRepository
    private final AuthorRepository authorRepository
    private final IngredientRepository ingredientRepository
    private final RecipeIngredientRepository recipeIngredientRepository

    DataUtil(RecipeRepository recipeRepository,
             AuthorRepository authorRepository,
             IngredientRepository ingredientRepository,
             RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeRepository = recipeRepository
        this.authorRepository = authorRepository
        this.ingredientRepository = ingredientRepository
        this.recipeIngredientRepository = recipeIngredientRepository
    }

    Optional<RecipeEntity> getRecipeById(Integer id) {
         recipeRepository.findById(id)
    }

    List<RecipeIngredientEntity> getRecipeIngredientIdsByRecipeId(Integer id) {
         recipeIngredientRepository.findAllByRecipeId(id);
    }

    Optional<IngredientEntity> getIngredientById(Integer id) {
        ingredientRepository.findById(id)
    }

    Optional<AuthorEntity> getAuthorByNameAndEmail(String name, String email) {
        authorRepository.findByNameAndEmail(name, email)
    }
}
