package com.food.recipe.service;

import com.food.recipe.dto.v1.request.Author;
import com.food.recipe.dto.v1.request.RecipeRequest;
import com.food.recipe.exception.RequestValidationException;
import com.food.recipe.mapper.ModelToEntityMapper;
import com.food.recipe.persistance.entity.RecipeEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.food.recipe.mapper.ModelToDTOMapper.map;
import static java.lang.String.format;

/**
 * <b>ValidatorService</b> class holds custom validations required.
 * To save the further burden on the other class, prefer to do all the validations ahead.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ValidatorService {
    private final DataService dataService;

    /**
     * Validates if the recipe already exists for given user - so that they can update rather than inserting again
     *
     * @param recipe recipe details
     * @throws RequestValidationException if doesn't satisfy the condition
     */
    public void validate(RecipeRequest recipe) throws RequestValidationException {
        Author author = recipe.getAuthor();
        String recipeName = recipe.getName();
        if (dataService.isRecipeExistsForAuthor(map(author), recipeName)) {
            log.error("Already Recipe exists for given name: {} and author: {}", recipeName, author);
            throw new RequestValidationException("Recipe with the same name already exists for given author!");
        }
    }

    /**
     * @param id     recipe id
     * @param author author detials contains name and email
     * @throws RequestValidationException if doesn't satisfy the condition
     */
    public void validate(int id, Author author) throws RequestValidationException {
        RecipeEntity recipeEntity = validate(id);

        com.food.recipe.model.Author creator = ModelToEntityMapper.map(recipeEntity.getAuthor());
        com.food.recipe.model.Author updater = map(author);

        if (!creator.equals(updater)) {
            log.error("Recipe id: {}, trying to update by wrong author: {}, original author: {}", id,
                    updater, creator);
            throw new RequestValidationException(format("Recipe does not belongs to given user to perform action: %d", id));
        }
    }

    /**
     *
     * @param id recipe id
     * @return recipeEntity  - doesn't go outside of this class used inside here itself
     * @throws RequestValidationException  if the recipe is not found
     */
    public RecipeEntity validate(int id) throws RequestValidationException  {
        Optional<RecipeEntity> optionalRecipe = dataService.findRecipeById(id);

        if (optionalRecipe.isEmpty()) {
            log.error("Recipe does not exists for given id: {}", id);
            throw new RequestValidationException(format("Recipe does not exists for given id: %d", id));
        }
        return optionalRecipe.get();
    }
}
