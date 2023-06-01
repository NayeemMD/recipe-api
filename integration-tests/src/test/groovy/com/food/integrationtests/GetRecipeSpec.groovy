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
class GetRecipeSpec extends  Specification {
    @Autowired
    RestApi restApi

    @Autowired
    ObjectMapper objectMapper

    def 'given unknown id - when getting a recipe details  - then returns BAD response'() {
        given:
        def unknownId = -1

        when:
            var responseEntity = restApi.getRecipe(unknownId).block()
            APIError apiError = responseEntity.body

        then:
            responseEntity.statusCode.is4xxClientError()
            apiError.errors.first() == "Recipe does not exists for given id: $unknownId"
    }

    def 'given known id - when getting a recipe details  - then returns successful response'() {
        given:
            def authorRequest = new Author()
            authorRequest.email = "get_id_author@email.com"
            authorRequest.name = "get_id_author"
            def request = getCreateRecipeRequest(authorRequest)
            def requestBody = objectMapper.writeValueAsString(request)
        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response1 = responseEntity1.body

        when:
            var responseEntity = restApi.getRecipe(response1.id).block()
            var response = responseEntity.body

        then:
            responseEntity.statusCode.is2xxSuccessful()
        and: 'validate individual details'
            response1 == response
        and: 'then delete it'
            var deleted =  restApi.deleteRecipe(response1.id,
                objectMapper.writeValueAsString(authorRequest)).block()
            deleted.statusCode.is2xxSuccessful()
    }

    RecipeRequest getCreateRecipeRequest(author) {
        def request = new RecipeRequest()
        request.setAuthor(author)
        request.setName('get-single-recipe')
        request.setServings(10)
        request.setInstructions("instruction for adding a new recipe for you!")
        request.setIngredients(List.of("Ing1", "Ing2", "Ing3"))
        request.setType(RecipeType.VEGAN)
        request
    }

}
