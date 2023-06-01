package com.food.integrationtests

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.food.integrationtests.config.ApplicationContext
import com.food.integrationtests.config.RestApi
import com.food.integrationtests.v1.request.Author
import com.food.integrationtests.v1.request.QueryOperator
import com.food.integrationtests.v1.request.RecipeRequest
import com.food.integrationtests.v1.request.RecipeType
import com.food.integrationtests.v1.response.RecipeDTO
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import static com.food.integrationtests.util.SearchUtil.*

@Slf4j
@ContextConfiguration(classes = [ApplicationContext])
class SearchRecipeSpec extends  Specification {
    @Autowired
    RestApi restApi

    @Autowired
    ObjectMapper objectMapper

    def 'given recipe - when searching with name - then respond with successful response'() {
        given:
        def authorRequest = new Author()
        authorRequest.email = "search_id_author@email.com"
        authorRequest.name = "search_id_author"
        def recipeName = "search-with-name"
        def request = getCreateRecipeRequest(authorRequest, recipeName)
        def requestBody = objectMapper.writeValueAsString(request)
        and:
        var responseEntity1 = restApi.addRecipe(requestBody).block()
        RecipeDTO response1 = responseEntity1.body
        var searchRequest = prepareSearchRequest(response1.name)
        def searchBody = objectMapper.writeValueAsString(searchRequest)

        when:
        var responseEntity = restApi.searchRecipe(searchBody).block()
        List<RecipeDTO> response = objectMapper.readValue(responseEntity.body, new TypeReference<List<RecipeDTO>>() {});

        then:
        response.size() == 1
        response.first().name == response1.name
        response.first().id == response1.id
        response.first().servings == response1.servings
        response.first().ingredients == response1.ingredients
        response.first().instructions == response1.instructions
        response.first().author == response1.author
        and: 'then delete it'
        var deleted = restApi.deleteRecipe(response1.id,
                objectMapper.writeValueAsString(authorRequest)).block()
        deleted.statusCode.is2xxSuccessful()
    }

    @Unroll
    def 'given recipe - when searching with ingredientName - then respond with successful response'(String ingredient) {
        given:
            def authorRequest = new Author()
            authorRequest.email = "search_id_ingredient@email.com"
            authorRequest.name = "search_id_author_ingredientName"
            def recipeName = "search-with-search_id_author_ingredientName_2"
            def request = getCreateRecipeRequest(authorRequest, recipeName, ["Ing1", "Ing2", "Ing3"])
            def requestBody = objectMapper.writeValueAsString(request)
        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response1 = responseEntity1.body
            var searchRequest = prepareSearchRequestWithIngredientNameAndRecipeName(ingredient, response1.name)
            def searchBody = objectMapper.writeValueAsString(searchRequest)

        when:
            var responseEntity = restApi.searchRecipe(searchBody).block()
            List<RecipeDTO> response = objectMapper.readValue(responseEntity.body, new TypeReference<List<RecipeDTO>>() {});

        then:
            response.size() == 1
            response.first().name == response1.name
            response.first().id == response1.id
            response.first().servings == response1.servings
            response.first().ingredients == response1.ingredients
            response.first().instructions == response1.instructions
            response.first().author == response1.author
        and: 'then delete it'
            var deleted = restApi.deleteRecipe(response1.id,
                    objectMapper.writeValueAsString(authorRequest)).block()
            deleted.statusCode.is2xxSuccessful()
        where:
            ingredient << ["Ing1", "Ing2", "Ing3"]
    }

    @Unroll
    def 'given recipe - when searching with recipe Name and contains instructions - then respond with successful response'() {
        given:
            def authorRequest = new Author()
            authorRequest.email = "search_id_instrction1@email.com"
            authorRequest.name = "search_id_author_instruction_search102"
            def recipeName = "search-with-searhc_name_instructi12"
            def request = getCreateRecipeRequest(authorRequest, recipeName, ["Ing1", "Ing2", "Ing3"], "funny name for you")
            def requestBody = objectMapper.writeValueAsString(request)
        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response1 = responseEntity1.body
            var searchRequest = prepareSearchRequestWithInstructionAndRecipeName(instruction, response1.name)
            def searchBody = objectMapper.writeValueAsString(searchRequest)

        when:
            var responseEntity = restApi.searchRecipe(searchBody).block()
            List<RecipeDTO> response = objectMapper.readValue(responseEntity.body, new TypeReference<List<RecipeDTO>>() {});

        then:
            response.size() == 1
            response.first().name == response1.name
            response.first().id == response1.id
            response.first().servings == response1.servings
            response.first().ingredients == response1.ingredients
            response.first().instructions == response1.instructions
            response.first().author == response1.author
        and: 'then delete it'
            var deleted = restApi.deleteRecipe(response1.id,
                    objectMapper.writeValueAsString(authorRequest)).block()
            deleted.statusCode.is2xxSuccessful()
        where:
            instruction << ["funny", "name for you"]

    }

    @Unroll
    def 'given recipe - when searching with recipe Name and type - then respond with successful response'() {
        given:
            def authorRequest = new Author()
            authorRequest.email = "search_id_type@email.com"
            authorRequest.name = "search_id_author_type_search1023"
            def recipeName = "search-with-searhc_name_type"
            def request = getCreateRecipeRequest(authorRequest, recipeName, ["Ing1", "Ing2", "Ing3"])
            def requestBody = objectMapper.writeValueAsString(request)
        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response1 = responseEntity1.body
            var searchRequest = prepareSearchRequestWithType(RecipeType.VEGAN, response1.name)
            def searchBody = objectMapper.writeValueAsString(searchRequest)

        when:
            var responseEntity = restApi.searchRecipe(searchBody).block()
            List<RecipeDTO> response = objectMapper.readValue(responseEntity.body, new TypeReference<List<RecipeDTO>>() {});

        then:
            response.size() == 1
            response.first().name == response1.name
            response.first().id == response1.id
            response.first().servings == response1.servings
            response.first().ingredients == response1.ingredients
            response.first().instructions == response1.instructions
            response.first().author == response1.author
        and: 'then delete it'
            var deleted = restApi.deleteRecipe(response1.id,
                    objectMapper.writeValueAsString(authorRequest)).block()
            deleted.statusCode.is2xxSuccessful()
    }

    @Unroll
    def 'given recipe - when searching with recipe Name and serving quantity - then respond with successful response'() {
        given:
            def authorRequest = new Author()
            authorRequest.email = "search_id_servings@email.com"
            authorRequest.name = "search_id_author_servings_search3242"
            def recipeName = "search-with-searhc_servings_type"
            def request = getCreateRecipeRequest(authorRequest, recipeName, ["Ing1", "Ing2", "Ing3"],
                    "instruction to be changed", 7000)
            def requestBody = objectMapper.writeValueAsString(request)
        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response1 = responseEntity1.body
            var searchRequest = prepareSearchRequestWithServings(servings, queryOperator )
            def searchBody = objectMapper.writeValueAsString(searchRequest)

        when:
            var responseEntity = restApi.searchRecipe(searchBody).block()
            List<RecipeDTO> response = objectMapper.readValue(responseEntity.body, new TypeReference<List<RecipeDTO>>() {});

        then:
            response.size() == size
        if(size != 0) {
           assert response.first().name == response1.name
            assert response.first().id == response1.id
            assert response.first().servings == response1.servings
            assert response.first().ingredients == response1.ingredients
            assert response.first().instructions == response1.instructions
            assert response.first().author == response1.author
        }
        and: 'then delete it'
            var deleted = restApi.deleteRecipe(response1.id,
                    objectMapper.writeValueAsString(authorRequest)).block()
            deleted.statusCode.is2xxSuccessful()
        where:
            servings |  queryOperator               || size
            7000       |  QueryOperator.EQUALS       || 1
            6999       | QueryOperator.GREATER_THAN  || 1
            7010       | QueryOperator.GREATER_THAN  || 0
            -100        |  QueryOperator.LESS_THAN    || 0
    }


    RecipeRequest getCreateRecipeRequest(author, recipeName,
                                         ingredient = ["Ing1", "Ing2", "Ing3"],
    instruction = "instruction for adding a new recipe for you!",
    servings  = 10) {
        def request = new RecipeRequest()
        request.setAuthor(author)
        request.setName(recipeName)
        request.setServings(servings)
        request.setInstructions(instruction)
        request.setIngredients(ingredient)
        request.setType(RecipeType.VEGAN)
        request
    }

}
