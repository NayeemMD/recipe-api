package com.food.integrationtests

import com.food.integrationtests.config.ApplicationContext
import com.food.integrationtests.v1.error.APIError
import com.food.integrationtests.v1.request.Author
import com.fasterxml.jackson.databind.ObjectMapper
import com.food.integrationtests.config.RestApi
import com.food.integrationtests.v1.request.RecipeRequest
import com.food.integrationtests.v1.request.RecipeType
import com.food.integrationtests.v1.response.RecipeDTO
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static com.food.integrationtests.util.AssertUtil.validateResponseDTO
import static com.food.integrationtests.util.TestUtil.findFieldMessage

@Slf4j
@ContextConfiguration(classes = [ApplicationContext])
class AddRecipeSpec  extends Specification {

    @Autowired
    RestApi restApi

    @Autowired
    ObjectMapper objectMapper

    def 'given bad request - when adding a recipe - then should respond with bad request'() {
        given:

            def request = new RecipeRequest()
            request.setAuthor(new Author())
            def requestBody = objectMapper.writeValueAsString(request)
        when:
            var responseEntity = restApi.addRecipe(requestBody).block()

        then:
            responseEntity.statusCode.value() == 400
            APIError apiError = responseEntity.body
            apiError.errors.size() == 7
            findFieldMessage(apiError.errors, "RecipeRequest Field -'name'") == "RecipeRequest Field -'name' must not be empty"
            findFieldMessage(apiError.errors, "Author Field - 'name'") == "Author Field - 'name' must not be blank"
            findFieldMessage(apiError.errors, 'servings') == "Field - 'servings' must be greater than or equal to 1"
            findFieldMessage(apiError.errors, 'instructions') == "Field - 'instructions' must not be blank"
            findFieldMessage(apiError.errors, 'ingredients') == "Field - 'ingredients' must not be empty"
            findFieldMessage(apiError.errors, 'type') == "Field - 'type' must not be blank"
            findFieldMessage(apiError.errors, 'email') == "Field - 'email' must not be blank"
    }

    def 'given valid request - when adding a recipe - then returns created response'() {
        given:
            def author = new Author()
            author.name = "author-name-8"
            author.email = "authoremail@gmail.com"
            def request = getCreateRecipeRequest(author)
            def requestBody = objectMapper.writeValueAsString(request)
            def des = objectMapper.readValue(requestBody, RecipeRequest)
        when:
            var responseEntity = restApi.addRecipe(requestBody).block()
            RecipeDTO response = responseEntity.body
        then:
            responseEntity.statusCode.is2xxSuccessful()
            and: 'validate individual details'
            validateResponseDTO(response, request)
        and: 'then delete it'
            var deleted =  restApi.deleteRecipe(response.id, objectMapper.writeValueAsString(author)).block()
            deleted.statusCode.is2xxSuccessful()
    }


    RecipeRequest getCreateRecipeRequest(author) {
        def request = new RecipeRequest()
        request.setAuthor(author)
        request.setName('add-recipe')
        request.setServings(10)
        request.setInstructions("instruction for adding a new recipe for you!")
        request.setIngredients(List.of("Ing1", "Ing2", "Ing3"))
        request.setType(RecipeType.VEGETARIAN)
        request
    }

}
