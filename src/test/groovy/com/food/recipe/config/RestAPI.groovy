package com.food.recipe.config

import com.food.recipe.dto.v1.error.APIError
import com.food.recipe.dto.v1.response.PageRecipeDTO
import com.food.recipe.dto.v1.response.RecipeDTO
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.MediaType

import static com.food.recipe.dto.v1.MediaType.COM_FOOD_RECIPE_V1_JSON

class RestAPI {
    private final TestRestTemplate restTemplate
    private final Integer port

    RestAPI(TestRestTemplate restTemplate, Integer port) {
        this.restTemplate = restTemplate
        this.port = port
        this.restTemplate.getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory())
    }

    ResponseEntity<RecipeDTO> addRecipe(String body) {
         restTemplate.exchange(
                "http://localhost:$port/recipes",
                HttpMethod.POST,
                new HttpEntity<>(body, customHeader()),
                RecipeDTO.class)

    }

    ResponseEntity<APIError> addRecipeForError(String body) {
         restTemplate.exchange(
                "http://localhost:$port/recipes",
                HttpMethod.POST,
                new HttpEntity<>(body, customHeader()),
                APIError.class)

    }

    static HttpHeaders customHeader() {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.set("Accept", COM_FOOD_RECIPE_V1_JSON)
        headers
    }

    ResponseEntity<RecipeDTO> updateRecipe(int id, String body) {
        restTemplate.exchange(
                "http://localhost:$port/recipes/$id",
                HttpMethod.PATCH,
                new HttpEntity<>(body, customHeader()),
                RecipeDTO.class)
    }

    ResponseEntity<APIError> updateRecipeForError(int id, String body) {
        restTemplate.exchange(
                "http://localhost:$port/recipes/$id",
                HttpMethod.PATCH,
                new HttpEntity<>(body, customHeader()),
                APIError.class)
    }

    ResponseEntity<APIError> deleteRecipeForError(int id, String body) {
        restTemplate.exchange(
                "http://localhost:$port/recipes/$id",
                HttpMethod.DELETE,
                new HttpEntity<>(body, customHeader()),
                APIError.class)
    }

    ResponseEntity<APIError> getRecipeForError(int id) {
        restTemplate.exchange(
                "http://localhost:$port/recipes/$id",
                HttpMethod.GET,
                new HttpEntity<>(customHeader()),
                APIError.class)
    }

    ResponseEntity<RecipeDTO> getRecipe(int id) {
        restTemplate.exchange(
                "http://localhost:$port/recipes/$id",
                HttpMethod.GET,
                new HttpEntity<>(customHeader()),
                RecipeDTO.class)
    }

    ResponseEntity<Void> deleteRecipe(int id, String body) {
        restTemplate.exchange(
                "http://localhost:$port/recipes/$id",
                HttpMethod.DELETE,
                new HttpEntity<>(body, customHeader()),
                Void.class)
    }

    ResponseEntity<PageRecipeDTO> getPageRecipes(int pageNumber,int limit) {
        restTemplate.exchange(
                "http://localhost:$port/recipes?pageNumber=$pageNumber&limit=$limit",
                HttpMethod.GET,
                new HttpEntity<>(customHeader()),
                PageRecipeDTO.class)
    }

    ResponseEntity<List<RecipeDTO>> searchRecipe(String body) {
        restTemplate.exchange(
                "http://localhost:$port/recipes/search",
                HttpMethod.GET,
                new HttpEntity<>(body, customHeader()),
                new ParameterizedTypeReference<List<RecipeDTO>>() {})
    }
}
