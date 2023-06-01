package com.food.recipe.stub

import com.food.recipe.dto.v1.request.Author
import com.food.recipe.dto.v1.request.RecipeRequest
import com.food.recipe.model.RecipeType
import com.github.javafaker.Faker

class RequestDataStub {
    Faker faker = new Faker()

     RecipeRequest getCreateRecipeRequest(RecipeType type) {
         def request = new RecipeRequest()
         request.setAuthor(getAuthor())
         request.setName(faker.funnyName().name())
         request.setServings(faker.number().numberBetween(2,10))
         request.setInstructions(faker.lorem().paragraph(2))
         request.setIngredients(getIngredients())
         request.setType(type)
         request
     }

    RecipeRequest updateRecipeDetailsWithoutModifyingIngredients(RecipeRequest recipe) {
        recipe.name = faker.funnyName().name()
        recipe.servings = faker.number().numberBetween(0,10)
        recipe.instructions = faker.lorem().paragraph(2)
        recipe.ingredients = List.of(faker.food().ingredient(), faker.food().ingredient(), faker.food().ingredient())
        recipe
    }

    List<String> getIngredients() {
        List.of(faker.food().ingredient(), faker.food().ingredient(), faker.food().ingredient())
    }


    Author getAuthor() {
        def author = new Author()
        author.setEmail(faker.internet().emailAddress())
        author.setName(faker.artist().name())
        author
    }
}
