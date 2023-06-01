package com.food.integrationtests

import com.fasterxml.jackson.databind.ObjectMapper
import com.food.integrationtests.config.ApplicationContext
import com.food.integrationtests.config.RestApi
import com.food.integrationtests.v1.error.APIError
import com.food.integrationtests.v1.request.Author
import com.food.integrationtests.v1.request.RecipeRequest
import com.food.integrationtests.v1.request.RecipeType
import com.food.integrationtests.v1.response.RecipeDTO
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@Slf4j
@ContextConfiguration(classes = [ApplicationContext])
class UpdateRecipeSpec extends  Specification {
    @Autowired
    RestApi restApi

    @Autowired
    ObjectMapper objectMapper

    def 'given existing recipe - when modifying by other author - then returns BAD response'() {
        given:
            def authorRequest = new Author()
            authorRequest.email = "update_id1_author@email.com"
            authorRequest.name = "update_id_author"
            def author2 = new Author()
            author2.email = "fraud_id_author@email.com"
            author2.name = "fraud"
            def request = getCreateRecipeRequest(authorRequest)
            def requestBody = objectMapper.writeValueAsString(request)
            def fraudUserBody = objectMapper.writeValueAsString(author2)
        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response1 = responseEntity1.body
            request.author = author2
            def requestBody2 = objectMapper.writeValueAsString(request)

        when:
            var responseEntity = restApi.updateRecipe(response1.id, requestBody2).block()
            APIError apiError = responseEntity.body

        then:
            responseEntity.statusCode.is4xxClientError()
            apiError.errors.first() == "Recipe does not belongs to given user to perform action: $response1.id"
        and: 'then delete it'
            var deleted =  restApi.deleteRecipe(response1.id,
                    objectMapper.writeValueAsString(authorRequest)).block()
            deleted.statusCode.is2xxSuccessful()
    }

    def 'given existing recipe - when modifying by same author - then returns SUCCESSFUL response'() {
        given:
            def authorRequest = new Author()
            authorRequest.email = "update_id2_author@email.com"
            authorRequest.name = "update_id2_author"
            def request = getCreateRecipeRequest(authorRequest)
            def requestBody = objectMapper.writeValueAsString(request)
        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response1 = responseEntity1.body
            request.instructions = "modified for testing"
            def requestBody2 = objectMapper.writeValueAsString(request)

        when:
            var responseEntity = restApi.updateRecipe(response1.id, requestBody2).block()
            RecipeDTO recipeDTO = responseEntity.body

        then:
            responseEntity.statusCode.is2xxSuccessful()
            recipeDTO.instructions == "modified for testing"
        and: 'then delete it'
            var deleted =  restApi.deleteRecipe(response1.id,
                    objectMapper.writeValueAsString(authorRequest)).block()
            deleted.statusCode.is2xxSuccessful()
    }

    RecipeRequest getCreateRecipeRequest(author) {
        def request = new RecipeRequest()
        request.setAuthor(author)
        request.setName('update-single-recipe')
        request.setServings(10)
        request.setInstructions("instruction for adding a new recipe for you!")
        request.setIngredients(List.of("Ing1", "Ing2", "Ing3"))
        request.setType(RecipeType.NON_VEGETARIAN)
        request
    }

}
