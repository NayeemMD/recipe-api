package com.food.recipe.service;

import com.food.recipe.model.Recipe;
import com.food.recipe.model.RecipeSearch;
import com.food.recipe.model.SearchCriteria;
import com.food.recipe.model.SearchOperator;
import com.food.recipe.persistance.entity.AuthorEntity;
import com.food.recipe.persistance.entity.IngredientEntity;
import com.food.recipe.persistance.entity.RecipeEntity;
import com.food.recipe.persistance.entity.RecipeIngredientEntity;
import com.food.recipe.search.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.food.recipe.mapper.ModelToEntityMapper.map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    private final DataService dataService;

    @Transactional
    public Recipe upsertRecipe(Recipe recipe, Optional<Integer> reciepIdOptional) {
        AuthorEntity authorEntity = dataService.getOrElseCreate(recipe.getAuthor());
        RecipeEntity recipeEntity = map(recipe, authorEntity, reciepIdOptional);
        List<IngredientEntity> ingredientEntities = dataService.getOrElseCreate(recipe.getIngredients());
        RecipeEntity savedRecipe = dataService.save(recipeEntity, ingredientEntities, reciepIdOptional);
        recipe.setId(savedRecipe.getId());
        return recipe;
    }

    public void deleteRecipe(int id) {
        dataService.deleteRecipeDetails(id);
    }

    public Recipe getRecipeById(int id) {
        List<RecipeIngredientEntity> recipeDetails = dataService.getRecipeDetailsByRecipeId(id);
        return map(recipeDetails);
    }

    public Page<Recipe> getAllRecipes(int pageNumber, int limit) {
        PageRequest pageRequest = PageRequest.of(pageNumber, limit);
        Page<RecipeEntity> recipeEntities = dataService.findAllRecipesByPage(pageRequest);
        Map<RecipeEntity, List<IngredientEntity>> recipeDetails = getRecipeEntityListMap(new HashSet<>(recipeEntities.getContent()));
        return map(recipeEntities, recipeDetails);
    }

    private Map<RecipeEntity, List<IngredientEntity>> getRecipeEntityListMap(Set<RecipeEntity> recipeEntities) {
       return recipeEntities.stream()
                .collect(Collectors.groupingBy(
                        recipeEntity -> recipeEntity,
                        Collectors.mapping(
                                recipeEntity -> dataService.getIngredientEntitiesByRecipeId(recipeEntity.getId()),
                                Collectors.flatMapping(List::stream, Collectors.toList())
                        )
                ));
    }

    public List<Recipe> searchRecipes(RecipeSearch recipeSearch) {
        //can be parallelized and still get benefited
        List<RecipeEntity> recipeResults = getRecipeResults(recipeSearch.getSearchCriteriaList());
        List<RecipeEntity> authorResults = getAuthorResults(recipeSearch.getSearchCriteriaList());
        List<RecipeEntity> ingredientResults = getIngredientResults(recipeSearch.getSearchCriteriaList());
        Set<RecipeEntity> finalResults = new HashSet<>(recipeResults);

        //join the results
        if(recipeSearch.getOperator() == SearchOperator.AND) {
            retainAll(authorResults, finalResults);
            retainAll(ingredientResults, finalResults);
        } else {
            finalResults.addAll(authorResults);
            finalResults.addAll(ingredientResults);
        }

        return map(getRecipeEntityListMap(finalResults));
    }

    private static void retainAll(List<RecipeEntity> incoming, Set<RecipeEntity> finalResults) {
        if(!incoming.isEmpty() && !finalResults.isEmpty())
            finalResults.retainAll(incoming);
        if(finalResults.isEmpty() && !incoming.isEmpty())
            finalResults.addAll(incoming);
    }

    private List<RecipeEntity> getIngredientResults(List<SearchCriteria> searchCriteriaList) {
        List<SearchCriteria> recipeSearchList = searchCriteriaList.stream()
                .filter(searchCriteria -> searchCriteria.property().name().contains("INGREDIENT"))
                .toList();

        if(recipeSearchList.isEmpty()) return Collections.emptyList();

        SpecificationBuilder<IngredientEntity> builder = new SpecificationBuilder<>();
        Specification<IngredientEntity> specification = builder.build(recipeSearchList);
        List<IngredientEntity> ingredientEntities = dataService.findAllIngredients(specification);
        return ingredientEntities.stream().flatMap(ingredient -> dataService.getRecipeDetailsByIngredientId(ingredient.getId()).stream())
                .map(RecipeIngredientEntity::getRecipe)
                .collect(Collectors.toList());
    }

    private List<RecipeEntity> getAuthorResults(List<SearchCriteria> searchCriteriaList) {
        List<SearchCriteria> recipeSearchList = searchCriteriaList.stream()
                .filter(searchCriteria -> searchCriteria.property().name().contains("AUTHOR"))
                .toList();

        if(recipeSearchList.isEmpty()) return Collections.emptyList();

        SpecificationBuilder<AuthorEntity> builder = new SpecificationBuilder<>();
        Specification<AuthorEntity> specification = builder.build(recipeSearchList);
        List<AuthorEntity> authors = dataService.findAllAuthors(specification);
        return authors.stream().flatMap(author -> dataService.findRecipeByAuthorId(author.getId()))
                .collect(Collectors.toList());
    }

    private List<RecipeEntity> getRecipeResults(List<SearchCriteria> searchCriteriaList) {
        List<SearchCriteria> recipeSearchList = searchCriteriaList.stream()
                .filter(searchCriteria -> !searchCriteria.property().name().contains("_"))
                .toList();

        if(recipeSearchList.isEmpty()) return Collections.emptyList();

        SpecificationBuilder<RecipeEntity> builder = new SpecificationBuilder<>();
        Specification<RecipeEntity> specification = builder.build(recipeSearchList);
        return dataService.findAllRecipes(specification);
    }
}
