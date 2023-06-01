package com.food.recipe.service;

import com.food.recipe.mapper.ModelToEntityMapper;
import com.food.recipe.model.Author;
import com.food.recipe.model.Ingredient;
import com.food.recipe.persistance.AuthorRepository;
import com.food.recipe.persistance.IngredientRepository;
import com.food.recipe.persistance.RecipeIngredientRepository;
import com.food.recipe.persistance.RecipeRepository;
import com.food.recipe.persistance.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <b>DataService</b> class acts an abstraction for data related activities
 * This can host repositories and rest client we need in future
 * Try not do any business logic in this class, keep it as data layer
 */
@Service
@RequiredArgsConstructor
public class DataService {
    private final RecipeRepository recipeRepository;
    private final AuthorRepository authorRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    /**
     * Check if recipe exists for given name and author details
     *
     * @param author     author details contains name, and email
     * @param recipeName recipe name
     * @return true if exists
     */

    public boolean isRecipeExistsForAuthor(Author author, String recipeName) {
        Optional<AuthorEntity> authorOptional = findAuthor(author);
        return authorOptional
                .filter(authorEntity -> findRecipeByNameAndAuthorId(recipeName, authorEntity.getId()).isPresent())
                .isPresent();

    }

    /**
     * save teh recipe, ingredients, recipe_Ingredient - but if the recipe id is provided then consider it as update
     * If it is update - then update the ingredient details accordingly
     *
     * @param recipe             - recipe details
     * @param ingredientEntities - ingredient details
     * @param receiptId          - determine if it is update or not
     * @return - saved recipe entity
     */
    public RecipeEntity save(RecipeEntity recipe, List<IngredientEntity> ingredientEntities, Optional<Integer> receiptId) {
        receiptId.ifPresent(integer -> updateIngredientsEntityMappings(ingredientEntities, integer));
        RecipeEntity savedRecipe = recipeRepository.save(recipe);
        List<IngredientEntity> savedIngredients = ingredientRepository.saveAll(ingredientEntities);
        List<RecipeIngredientEntity> entities = savedIngredients.stream()
                .map(ingredientEntity -> new RecipeIngredientEntity(ingredientEntity, savedRecipe)
                ).toList();
        recipeIngredientRepository.saveAll(entities);
        return savedRecipe;
    }

    /**
     * Returns list of ingredients found for given recipe id
     *
     * @param recipeId recipe id
     * @return list of ingredients
     */
    public List<IngredientEntity> getIngredientEntitiesByRecipeId(Integer recipeId) {
        return getRecipeDetailsByRecipeId(recipeId)
                .stream().map(RecipeIngredientEntity::getIngredient).toList();
    }

    /**
     * Return recipe ingredients details for given recipe id
     *
     * @param recipeId recipe id
     * @return recipe ingredients details
     */
    public List<RecipeIngredientEntity> getRecipeDetailsByRecipeId(Integer recipeId) {
        return recipeIngredientRepository.findAllByRecipeId(recipeId);
    }

    /**
     * Returns recipe ingredients details for given ingredient id
     *
     * @param ingredientId ingredient ID
     * @return recipe ingredients details
     */
    public List<RecipeIngredientEntity> getRecipeDetailsByIngredientId(Integer ingredientId) {
        return recipeIngredientRepository.findAllByIngredientId(ingredientId);
    }

    /**
     * Return author entity which includes ID for given author object
     *
     * @param author contains name and email
     * @return author entity
     */
    public Optional<AuthorEntity> findAuthor(Author author) {
        return authorRepository.findByNameAndEmail(author.getName(), author.getEmail());
    }

    /**
     * @param name     recipe name
     * @param authorId author id
     * @return returns recipe entity if found, or else empty optional
     */
    private Optional<RecipeEntity> findRecipeByNameAndAuthorId(String name, Integer authorId) {
        return recipeRepository.findByNameAndAuthorId(name,
                authorId);
    }

    /**
     * @param name inngredient name
     * @return ingredient entity if found, or else empty optional
     */

    public Optional<IngredientEntity> findIngredientByName(String name) {
        return ingredientRepository.findByName(name);
    }

    /**
     * Returns Author entity if exists or else create and returns it
     *
     * @param author author details contains name, and email
     * @return author entity
     */
    public AuthorEntity getOrElseCreate(Author author) {
        return this.findAuthor(author)
                .orElseGet(() -> authorRepository.save(ModelToEntityMapper.map(author)));
    }

    /**
     * Returns Ingredient entity details if exists or else create and return that object
     *
     * @param ingredients ingredient name
     * @return ingredient entity
     */
    public List<IngredientEntity> getOrElseCreate(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> this.findIngredientByName(ingredient.getName())
                        .orElseGet(() -> ingredientRepository.save(ModelToEntityMapper.map(ingredient))))
                .toList();

    }

    /**
     * @param id recipe Id
     * @return optional of recipe entity
     */
    public Optional<RecipeEntity> findRecipeById(int id) {
        return recipeRepository.findById(id);
    }

    /**
     * Deletes recipe details for given recipe id
     *
     * @param id recipe id
     */
    public void deleteRecipeDetails(int id) {
        recipeIngredientRepository.deleteAll(getRecipeDetailsByRecipeId(id));
        recipeRepository.deleteById(id);
    }

    /**
     * Page querying for recipe details
     *
     * @param pageRequest contains page number and limit
     * @return matched page recipe entities
     */
    public Page<RecipeEntity> findAllRecipesByPage(PageRequest pageRequest) {
        return recipeRepository.findAll(pageRequest);
    }

    /**
     * @param specification specification executes the predicate to obtain the results
     * @return matched recipe entities
     */
    public List<RecipeEntity> findAllRecipes(Specification<RecipeEntity> specification) {
        return recipeRepository.findAll(specification);
    }

    /**
     * @param specification specification executes the predicate to obtain the results
     * @return matched author entities
     */
    public List<AuthorEntity> findAllAuthors(Specification<AuthorEntity> specification) {
        return authorRepository.findAll(specification);
    }

    /**
     * @param authorId return recipes for given author id
     * @return streamed recipe entities
     */
    public Stream<RecipeEntity> findRecipeByAuthorId(Integer authorId) {
        return recipeRepository.findAllByAuthorId(authorId).stream();
    }

    /**
     * @param specification specification executes the predicate to obtain the results
     * @return matched ingredient entities
     */
    public List<IngredientEntity> findAllIngredients(Specification<IngredientEntity> specification) {
        return ingredientRepository.findAll(specification);
    }

    private void updateIngredientsEntityMappings(List<IngredientEntity> updated, Integer recipeId) {
        List<IngredientEntity> saved = new ArrayList<>(getIngredientEntitiesByRecipeId(recipeId));
        saved.removeAll(updated);
        saved.forEach(ingredientEntity -> recipeIngredientRepository
                .deleteById(new RecipeIngredientId(recipeId, ingredientEntity.getId())));
    }
}
