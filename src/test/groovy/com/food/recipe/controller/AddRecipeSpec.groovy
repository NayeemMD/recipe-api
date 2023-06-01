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
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static com.food.recipe.util.AssertUtil.validateDatabaseEntries
import static com.food.recipe.util.AssertUtil.validateResponseDTO
import static com.food.recipe.util.TestUtil.findFieldMessage

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
class AddRecipeSpec extends Specification {
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

    def 'given valid request - when adding a recipe - then returns created response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def requestBody = objectMapper.writeValueAsString(request)
            def des = objectMapper.readValue(requestBody, RecipeRequest)
        when:
            var responseEntity = api.addRecipe(requestBody)
            var response = responseEntity.body
        then:
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(201))
        and: 'validate individual details'
            validateResponseDTO(response, request)
        and: 'validate Database entries'
            validateDatabaseEntries(response, dataUtil)

    }

    def 'given valid request - when adding a duplicate recipe - then returns bad response'() {
        given:
            def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
            def requestBody = objectMapper.writeValueAsString(request)
            var stubEntity = api.addRecipe(requestBody)
        when:
            var responseEntity = api.addRecipeForError(requestBody)
            var apiError = responseEntity.body
        then: 'stub should be successful'
            stubEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(201))
        and: 'duplicate request should fail'
            responseEntity.statusCode.isSameCodeAs(HttpStatusCode.valueOf(400))
            apiError.errors.size() == 1
            apiError.errors.get(0) == "Recipe with the same name already exists for given author!"
    }




}
