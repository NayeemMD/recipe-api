package com.food.integrationtests.config

import com.food.integrationtests.v1.MediaType
import com.food.integrationtests.v1.error.APIError
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import com.food.integrationtests.v1.response.RecipeDTO

class RestApi {
    public static final String RECIPE_URI = "/recipes"
    public static final String GET_RECIPE_URI = "/recipes/{id}"
    public static final String SEARCH_RECIPE_URI = "/recipes/search"

    RestApi(WebClient recipeRestServiceClient) {
        this.recipeRestServiceClient = recipeRestServiceClient
    }
    WebClient recipeRestServiceClient

    Mono<ResponseEntity<?>> addRecipe(String body) {
     return recipeRestServiceClient.post()
                .uri(RECIPE_URI)
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.ACCEPT, MediaType.COM_FOOD_RECIPE_V1_JSON)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(APIError.class)
                                .map(body1 -> ResponseEntity.badRequest().body(body1))
                    } else {
                        return response.bodyToMono(RecipeDTO.class)
                                .map(body1 -> ResponseEntity.ok(body1))
                    }
                })
    }


    Mono<ResponseEntity<?>> updateRecipe(int id, String body) {
        return recipeRestServiceClient.patch()
                .uri(uriBuilder ->
                        uriBuilder.path(GET_RECIPE_URI)
                                .build(id))
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.ACCEPT, MediaType.COM_FOOD_RECIPE_V1_JSON)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(APIError.class)
                                .map(body1 -> ResponseEntity.badRequest().body(body1))
                    } else {
                        return response.bodyToMono(RecipeDTO.class)
                                .map(body1 -> ResponseEntity.ok(body1))
                    }
                })
    }

    Mono<ResponseEntity<?>> deleteRecipe(int id, String body) {
        return recipeRestServiceClient.delete()
                .uri(uriBuilder ->
                        uriBuilder.path(GET_RECIPE_URI)
                                .build(id))
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.ACCEPT, MediaType.COM_FOOD_RECIPE_V1_JSON)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(APIError.class)
                                .map(body1 -> ResponseEntity.badRequest().body(body1))
                    } else {
                        return Mono.justOrEmpty(ResponseEntity.ok())
                    }
                })
    }



    Mono<ResponseEntity<?>>  getRecipe(int id) {
         recipeRestServiceClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(GET_RECIPE_URI)
                                .build(id))
                .header(HttpHeaders.ACCEPT, MediaType.COM_FOOD_RECIPE_V1_JSON)
                 .exchangeToMono(response -> {
                     if (!response.statusCode().is2xxSuccessful()) {
                         return response.bodyToMono(APIError.class)
                                 .map(body1 -> ResponseEntity.badRequest().body(body1))
                     } else {
                         return response.bodyToMono(RecipeDTO.class)
                                 .map(body1 -> ResponseEntity.ok(body1))
                     }
                 })
    }

    Mono<ResponseEntity<?>>  getPageRecipes(int pageNumber, int limit) {
         recipeRestServiceClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(RECIPE_URI)
                                .queryParam("pageNumber", pageNumber)
                                .queryParam("limit", limit)
                                .build())
                .header(HttpHeaders.ACCEPT, MediaType.COM_FOOD_RECIPE_V1_JSON)
                 .exchangeToMono(response -> {
                     if (!response.statusCode().is2xxSuccessful()) {
                         return response.bodyToMono(APIError.class)
                                 .map(body1 -> ResponseEntity.badRequest().body(body1))
                     } else {
                         return response.bodyToMono(String.class)
                                 .map(body1 -> ResponseEntity.ok(body1))
                     }
                 })
    }

    Mono<ResponseEntity<?>>  searchRecipe(String body) {
        recipeRestServiceClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(SEARCH_RECIPE_URI)
                                .build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.ACCEPT, MediaType.COM_FOOD_RECIPE_V1_JSON)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(APIError.class)
                                .map(body1 -> ResponseEntity.badRequest().body(body1))
                    } else {
                        return response.bodyToMono(String.class)
                                .map(body1 -> ResponseEntity.ok(body1))
                    }
                })
    }
}
