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

@Service
@RequiredArgsConstructor
public class DataService {
    private final RecipeRepository recipeRepository;
    private final AuthorRepository authorRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;


    public boolean isRecipeExistsForAuthor(Author author, String recipeName) {
        Optional<AuthorEntity> authorOptional = findAuthor(author);
        return authorOptional
                .filter(authorEntity -> findRecipeByNameAndAuthorId(recipeName, authorEntity.getId()).isPresent())
                .isPresent();

    }

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

    private void updateIngredientsEntityMappings(List<IngredientEntity> updated, Integer recipeId) {
        List<IngredientEntity> saved = new ArrayList<>(getIngredientEntitiesByRecipeId(recipeId));
        saved.removeAll(updated);
        saved.forEach(ingredientEntity -> recipeIngredientRepository
                .deleteById(new RecipeIngredientId(recipeId, ingredientEntity.getId())));
    }

    public List<IngredientEntity> getIngredientEntitiesByRecipeId(Integer recipeId) {
        return getRecipeDetailsByRecipeId(recipeId)
                .stream().map(RecipeIngredientEntity::getIngredient).toList();
    }

    public List<RecipeIngredientEntity> getRecipeDetailsByRecipeId(Integer recipeId) {
        return recipeIngredientRepository.findAllByRecipeId(recipeId);
    }

    public List<RecipeIngredientEntity> getRecipeDetailsByIngredientId(Integer recipeId) {
        return recipeIngredientRepository.findAllByIngredientId(recipeId);
    }

    public Optional<AuthorEntity> findAuthor(Author author) {
        return authorRepository.findByNameAndEmail(author.getName(), author.getEmail());
    }

    private Optional<RecipeEntity> findRecipeByNameAndAuthorId(String name, Integer authorId) {
        return recipeRepository.findByNameAndAuthorId(name,
                authorId);
    }

    public Optional<IngredientEntity> findIngredientByName(String name) {
        return ingredientRepository.findByName(name);
    }

    public AuthorEntity getOrElseCreate(Author author) {
        return this.findAuthor(author)
                .orElseGet(() -> authorRepository.save(ModelToEntityMapper.map(author)));
    }

    public List<IngredientEntity> getOrElseCreate(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> this.findIngredientByName(ingredient.getName())
                        .orElseGet(() -> ingredientRepository.save(ModelToEntityMapper.map(ingredient))))
                .toList();

    }

    public Optional<RecipeEntity> findRecipeById(int id) {
        return recipeRepository.findById(id);
    }

    public void deleteRecipeDetails(int id) {
        recipeIngredientRepository.deleteAll(getRecipeDetailsByRecipeId(id));
        recipeRepository.deleteById(id);
    }

    public Page<RecipeIngredientEntity> findAllRecipeIngredients(PageRequest pageRequest) {
        return recipeIngredientRepository.findAll(pageRequest);

    }

    public Page<RecipeEntity> findAllRecipesByPage(PageRequest pageRequest) {
        return recipeRepository.findAll(pageRequest);
    }

    public List<RecipeIngredientEntity> findAllRecipeIngredients(Specification<RecipeIngredientEntity> specification) {
        return recipeIngredientRepository.findAll(specification);
    }

    public List<RecipeEntity> findAllRecipes(Specification<RecipeEntity> specification) {
        return recipeRepository.findAll(specification);
    }

    public List<AuthorEntity> findAllAuthors(Specification<AuthorEntity> specification) {
        return authorRepository.findAll(specification);
    }

    public Stream<RecipeEntity> findRecipeByAuthorId(Integer authorId) {
        return recipeRepository.findAllByAuthorId(authorId).stream();
    }

    public List<IngredientEntity> findAllIngredients(Specification<IngredientEntity> specification) {
        return ingredientRepository.findAll(specification);
    }
}
