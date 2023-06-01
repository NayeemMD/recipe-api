package com.food.recipe.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.food.recipe.config.RestAPI
import com.food.recipe.dto.v1.request.Author
import com.food.recipe.dto.v1.request.RecipeRequest
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
import spock.lang.Specification
import org.springframework.transaction.annotation.Transactional

import static com.food.recipe.util.AssertUtil.validateDatabaseEntries
import static com.food.recipe.util.AssertUtil.validateResponseDTO
import static com.food.recipe.util.TestUtil.findFieldMessage

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
class UpdateRecipeSpec extends Specification {

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

    def 'given bad request - when adding a recipe - then should respond with bad request'() {
        given:
            def request = new RecipeRequest()
            request.setAuthor(new Author())
            def requestBody = objectMapper.writeValueAsString(request)

        when:
            var responseEntity = api.addRecipeForError(requestBody)
            var apiError = responseEntity.body

        then:
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(400))
            apiError.errors.size() == 7
            findFieldMessage(apiError.errors, "RecipeRequest Field -'name'") == "RecipeRequest Field -'name' must not be empty"
            findFieldMessage(apiError.errors, "Author Field - 'name'") == "Author Field - 'name' must not be blank"
            findFieldMessage(apiError.errors, 'servings') == "Field - 'servings' must be greater than or equal to 1"
            findFieldMessage(apiError.errors, 'instructions') == "Field - 'instructions' must not be blank"
            findFieldMessage(apiError.errors, 'ingredients') == "Field - 'ingredients' must not be empty"
            findFieldMessage(apiError.errors, 'type') == "Field - 'type' must not be blank"
            findFieldMessage(apiError.errors, 'email') == "Field - 'email' must not be blank"
    }

    def 'given bad request with email - when adding a recipe - then should respond with bad request'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.VEGAN)
            def author = dataStub.getAuthor()
            author.setEmail("IamInvalidEmailAddress")
            request.setAuthor(author)
            def requestBody = objectMapper.writeValueAsString(request)

        when:
            var responseEntity = api.addRecipeForError(requestBody)
            var apiError = responseEntity.body

        then:
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(400))
            apiError.errors.size() == 1
            findFieldMessage(apiError.errors, 'email') == "Field - 'email' must be valid"
    }

    def 'given existing recipe - when modifying by other author - then returns BAD response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def existing = objectMapper.writeValueAsString(request)
            var existingRecipeResponse = api.addRecipe(existing)
            var existingRecipe = existingRecipeResponse.body

        and: 'update existing recipe with details'
            var newRequest = dataStub.getCreateRecipeRequest(RecipeType.VEGAN)
            def requestBody = objectMapper.writeValueAsString(newRequest)

        when:
            var responseEntity = api.updateRecipeForError(existingRecipe.id, requestBody)
            var apiError = responseEntity.body

        then: 'stub should be successful'
            existingRecipeResponse.statusCode.isSameCodeAs(HttpStatusCode.valueOf(201))

        and: 'update request should be failure'
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(400))
            apiError.errors.first() == "Recipe does not belongs to given user to perform action: $existingRecipe.id"

        and: 'validate existing entries are not modified'
            validateDatabaseEntries(existingRecipe, dataUtil)
    }


    def 'given existing recipe - when updating recipe content - then returns ok response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def existing = objectMapper.writeValueAsString(request)
            var existingRecipeResponse = api.addRecipe(existing)
            var existingRecipe = existingRecipeResponse.body

        and: 'update existing recipe with details'
            var newRequest = dataStub.updateRecipeDetailsWithoutModifyingIngredients(request)
            def requestBody = objectMapper.writeValueAsString(newRequest)

        when:
            var responseEntity = api.updateRecipe(existingRecipe.id, requestBody)
            var recipeDTO = responseEntity.body

        then: 'stub should be successful'
            existingRecipeResponse.statusCode.isSameCodeAs(HttpStatusCode.valueOf(201))

        and: 'update request should be successful'
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(200))

        and: 'validate individual details'
            validateResponseDTO(recipeDTO, newRequest)

        and: 'validate Database entries'
            validateDatabaseEntries(recipeDTO, dataUtil)
    }

    def 'given existing recipe - when modifying ingredient content - then returns ok response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def existing = objectMapper.writeValueAsString(request)
            var existingRecipeResponse = api.addRecipe(existing)
            var existingRecipe = existingRecipeResponse.body

        and: 'update existing recipe with details'
            var newRequest = dataStub.getCreateRecipeRequest(RecipeType.VEGAN)
            newRequest.author = request.author
            def requestBody = objectMapper.writeValueAsString(newRequest)

        when:
            var responseEntity = api.updateRecipe(existingRecipe.id, requestBody)
            var recipeDTO = responseEntity.body

        then: 'stub should be successful'
            existingRecipeResponse.statusCode.isSameCodeAs(HttpStatusCode.valueOf(201))

        and: 'update request should be successful'
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(200))

        and: 'validate individual details'
            validateResponseDTO(recipeDTO, newRequest)

        and: 'validate Database entries'
            validateDatabaseEntries(recipeDTO, dataUtil)
    }
}
