package com.food.recipe.mapper;

import com.food.recipe.model.Author;
import com.food.recipe.model.Ingredient;
import com.food.recipe.model.Recipe;
import com.food.recipe.persistance.entity.AuthorEntity;
import com.food.recipe.persistance.entity.IngredientEntity;
import com.food.recipe.persistance.entity.RecipeEntity;
import com.food.recipe.persistance.entity.RecipeIngredientEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelToEntityMapper {

    public static AuthorEntity map(Author model) {
        AuthorEntity entity = new AuthorEntity();
        entity.setName(model.getName());
        entity.setEmail(model.getEmail());
        return entity;
    }

    public static Author map(AuthorEntity entity) {
        return Author.builder()
                .withEmail(entity.getEmail())
                .withName(entity.getName())
                .build();
    }

    public static RecipeEntity map(Recipe model, AuthorEntity authorEntity, Optional<Integer> optionalRecipeId) {
        RecipeEntity entity = new RecipeEntity();
        entity.setName(model.getName());
        entity.setType(model.getType());
        entity.setServings(model.getServings());
        entity.setInstructions(model.getInstructions());
        entity.setAuthor(authorEntity);
        optionalRecipeId.ifPresent(entity::setId);
        return entity;
    }


    public static IngredientEntity map(Ingredient ingredient) {
        IngredientEntity entity = new IngredientEntity();
        entity.setName(ingredient.getName());
        return entity;
    }

    public static List<Ingredient> mapToIngredients(List<IngredientEntity> ingredientEntities) {
        return ingredientEntities.stream().map(ingredientEntity ->
                Ingredient.builder().withName(ingredientEntity.getName()).build()
                ).toList();
    }

    public static Recipe map(List<RecipeIngredientEntity> recipeDetails) {
        RecipeEntity recipeEntity = recipeDetails.get(0).getRecipe();
        List<IngredientEntity> ingredientEntities = recipeDetails.stream()
                .map(RecipeIngredientEntity::getIngredient)
                .toList();
        return toRecipe(recipeEntity, ingredientEntities);
    }

    private static Recipe toRecipe(RecipeEntity recipeEntity, List<IngredientEntity> ingredientEntities) {
        return Recipe.builder()
                .withName(recipeEntity.getName())
                .withId(recipeEntity.getId())
                .withType(recipeEntity.getType())
                .withInstructions(recipeEntity.getInstructions())
                .withServings(recipeEntity.getServings())
                .withAuthor(map(recipeEntity.getAuthor()))
                .withIngredients(mapToIngredients(ingredientEntities))
                .build();
    }

    public static Page<Recipe> map(Page<RecipeEntity> recipeEntities,
                                   Map<RecipeEntity, List<IngredientEntity>>  recipeToIngredients) {

        List<Recipe> recipes = map(recipeToIngredients);

        return new PageImpl<>(recipes, recipeEntities.getPageable(), recipeEntities.getTotalElements());
    }

    public static List<Recipe> map(Map<RecipeEntity, List<IngredientEntity>> recipeToIngredients) {
        return recipeToIngredients.entrySet().stream()
                .map(entry -> toRecipe(entry.getKey(), entry.getValue())
                ).toList();
    }
}
