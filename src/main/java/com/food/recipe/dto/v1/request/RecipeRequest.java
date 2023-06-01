package com.food.recipe.dto.v1.request;

import com.food.recipe.model.RecipeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Objects;


public class RecipeRequest {
    @NotBlank(message = "RecipeRequest Field -'name' must not be empty")
    private String name;

    @NotNull(message = "Field - 'type' must not be blank")
    private RecipeType type;

    @Min(value = 1, message = "Field - 'servings' must be greater than or equal to 1")
    private int servings;

    @NotNull(message = "Field - 'instructions' must not be blank")
    @Size(min = 5, message = "Field - 'instructions' length must exceeds 5")
    private String instructions;

    @NotEmpty (message = "Field - 'ingredients' must not be empty")
    private List<@NotBlank String> ingredients;

    @Valid
    private Author author;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeType getType() {
        return type;
    }

    public void setType(RecipeType type) {
        this.type = type;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public RecipeRequest() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeRequest that = (RecipeRequest) o;

        if (servings != that.servings) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (type != that.type) return false;
        if (!Objects.equals(instructions, that.instructions)) return false;
        if (!Objects.equals(ingredients, that.ingredients)) return false;
        return Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + servings;
        result = 31 * result + (instructions != null ? instructions.hashCode() : 0);
        result = 31 * result + (ingredients != null ? ingredients.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        return result;
    }
}
