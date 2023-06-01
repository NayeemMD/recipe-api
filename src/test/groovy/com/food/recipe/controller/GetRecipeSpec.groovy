package com.food.recipe.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.food.recipe.config.RestAPI
import com.food.recipe.model.RecipeType
import com.food.recipe.stub.RequestDataStub
import com.food.recipe.util.DataUtil
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatusCode
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
class GetRecipeSpec extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private DataUtil dataUtil

    @LocalServerPort
    private Integer port = 0

    RestAPI api
    RequestDataStub dataStub
    ObjectMapper objectMapper

    def setup() {
        api = new RestAPI(restTemplate, port)
        dataStub = new RequestDataStub()
        objectMapper = new ObjectMapper()
    }

    def 'given unknown id - when getting a recipe details  - then returns BAD response'() {
        given:
            def unknownId = 100

         when:
            var responseEntity = api.getRecipeForError(unknownId)
            var apiError = responseEntity.body

        then:
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(400))
            apiError.errors.first() == "Recipe does not exists for given id: $unknownId"
    }

    def 'given known id - when getting a recipe details  - then returns successful response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def existing = objectMapper.writeValueAsString(request)
            var existingRecipeResponse = api.addRecipe(existing)
            var existingRecipe = existingRecipeResponse.body

        when:
            var responseEntity = api.getRecipe(existingRecipe.id)
            var response = responseEntity.body

        then:
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(200))

        and: 'validate individual details'
            existingRecipe.id == response.id
            existingRecipe.name == response.name
    }

}
