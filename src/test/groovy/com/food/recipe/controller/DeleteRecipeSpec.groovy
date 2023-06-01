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

import static com.food.recipe.util.AssertUtil.validateDatabaseEntries
import static com.food.recipe.util.AssertUtil.validateDatabaseEntriesForDelete
import static com.food.recipe.util.TestUtil.findFieldMessage

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
class DeleteRecipeSpec extends Specification {
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

    def 'given bad request with email - when deleting a recipe - then should respond with bad request'() {
        given:
            def author = dataStub.getAuthor()
            author.setEmail("IamInvalidEmailAddress")
            def requestBody = objectMapper.writeValueAsString(author)

        when:
            var responseEntity = api.deleteRecipeForError(1, requestBody)
            var apiError = responseEntity.body

        then:
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(400))
            apiError.errors.size() == 1
            findFieldMessage(apiError.errors, 'email') == "Field - 'email' must be valid"
    }

    def 'given existing recipe - when deleting by other author - then returns BAD response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def existing = objectMapper.writeValueAsString(request)
            var existingRecipeResponse = api.addRecipe(existing)
            var existingRecipe = existingRecipeResponse.body

        and: 'author who is trying to delete existing recipe'
            var newRequest = dataStub.getAuthor()
            def requestBody = objectMapper.writeValueAsString(newRequest)

        when:
            var responseEntity = api.deleteRecipeForError(existingRecipe.id, requestBody)
            var apiError = responseEntity.body

        then: 'stub should be successful'
            existingRecipeResponse.statusCode.isSameCodeAs(HttpStatusCode.valueOf(201))

        and: 'delete request should be failure'
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(400))
            apiError.errors.first() == "Recipe does not belongs to given user to perform action: $existingRecipe.id"

        and: 'validate existing entries are not modified'
            validateDatabaseEntries(existingRecipe, dataUtil)
    }

    def 'given existing recipe - when deleting by legit author - then returns NO_CONTENT response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def existing = objectMapper.writeValueAsString(request)
            var existingRecipeResponse = api.addRecipe(existing)
            var existingRecipe = existingRecipeResponse.body

        and: 'author who is trying to delete existing recipe'
            var newRequest = request.getAuthor()
            def requestBody = objectMapper.writeValueAsString(newRequest)

        when:
            var responseEntity = api.deleteRecipe(existingRecipe.id, requestBody)

        then: 'stub should be successful'
            existingRecipeResponse.statusCode.isSameCodeAs(HttpStatusCode.valueOf(201))

        and: 'delete request should be successful'
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(204))

        and: 'validate existing entries are not modified'
            validateDatabaseEntriesForDelete(existingRecipe, dataUtil)
    }
}
