package com.food.recipe.controller;

import com.food.recipe.dto.v1.request.Author;
import com.food.recipe.dto.v1.request.RecipeRequest;
import com.food.recipe.dto.v1.request.RecipeSearchRequest;
import com.food.recipe.dto.v1.response.PageRecipeDTO;
import com.food.recipe.dto.v1.response.RecipeDTO;
import com.food.recipe.model.Recipe;
import com.food.recipe.service.RecipeService;
import com.food.recipe.service.ValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.food.recipe.dto.v1.MediaType.COM_FOOD_RECIPE_V1_JSON;
import static com.food.recipe.mapper.ModelToDTOMapper.map;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;


@Slf4j
@Tag(name = "Recipes")
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    private final ValidatorService validator;

    @PostMapping(produces = COM_FOOD_RECIPE_V1_JSON)
    @Operation(summary = "Add a new recipe")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<RecipeDTO> addRecipe(@RequestBody @Valid RecipeRequest recipe) {
        validator.validate(recipe);
        Recipe createdRecipe = recipeService.upsertRecipe(map(recipe), Optional.empty());
        return ResponseEntity.status(HttpStatus.CREATED).body(map(createdRecipe));
    }

    @PatchMapping(value = "/{id}", produces = COM_FOOD_RECIPE_V1_JSON)
    @Operation(summary = "Update an existing recipe")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable int id,
                                                  @RequestBody @Valid RecipeRequest recipe) {
        validator.validate(id, recipe.getAuthor());
        Recipe updatedRecipe = recipeService.upsertRecipe(map(recipe), Optional.of(id));
        return ok(map(updatedRecipe));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "deleting an existing recipe")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<Void> deleteRecipe(@PathVariable int id,
                                             @RequestBody @Valid Author author) {
        validator.validate(id, author);
        recipeService.deleteRecipe(id);
        return noContent().build();
    }

    @GetMapping(value = "/{id}", produces = COM_FOOD_RECIPE_V1_JSON)
    @Operation(summary = "Get a recipe by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable int id) {
        validator.validate(id);
        Recipe recipe = recipeService.getRecipeById(id);
        return ok(map(recipe));
    }

    @GetMapping(produces = COM_FOOD_RECIPE_V1_JSON)
    @Operation(summary = "Get all recipes")
    public PageRecipeDTO getAllRecipes(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int limit) {
        Page<Recipe> recipes = recipeService.getAllRecipes(pageNumber, limit);
        return map(recipes);
    }

    @GetMapping(value = "/search", produces = COM_FOOD_RECIPE_V1_JSON)
    @Operation(summary = "Search recipes")
    public ResponseEntity<List<RecipeDTO>> searchRecipes( @RequestBody @Valid RecipeSearchRequest request) {
        List<Recipe> filteredRecipes = recipeService.searchRecipes(map(request));
        return ok(map(filteredRecipes));
    }
}
