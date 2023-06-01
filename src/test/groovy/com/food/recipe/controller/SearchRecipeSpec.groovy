package com.food.recipe.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.food.recipe.config.RestAPI
import com.food.recipe.dto.v1.response.RecipeDTO
import com.food.recipe.model.QueryOperator
import com.food.recipe.model.RecipeType
import com.food.recipe.model.SearchOperator
import com.food.recipe.stub.RequestDataStub
import com.food.recipe.util.DataUtil
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.food.recipe.util.SearchUtil.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
class SearchRecipeSpec extends Specification {
    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private DataUtil dataUtil

    @LocalServerPort
    private Integer port = 0

    @Shared
    boolean dataSetup = false

    @Shared
    RecipeDTO existingRecipe;

    RestAPI api
    RequestDataStub dataStub
    ObjectMapper objectMapper

    def setup() {
        api = new RestAPI(restTemplate, port)
        dataStub = new RequestDataStub()
        objectMapper = new ObjectMapper()
    }

    def 'given recipe - when searching with name - then respond with successful response'() {
        given:
            setupRecipes()
            var searchRequest = prepareSearchRequest(existingRecipe.name)
            def searchBody = objectMapper.writeValueAsString(searchRequest)

        when:
            var responseEntity = api.searchRecipe(searchBody)
            var response = responseEntity.body

        then:
            response.size() == 1
            response.first().name == existingRecipe.name
            response.first().id == existingRecipe.id
            response.first().servings == existingRecipe.servings
            response.first().ingredients == existingRecipe.ingredients
            response.first().instructions == existingRecipe.instructions
            response.first().author == existingRecipe.author
    }

    @Unroll
    def 'given recipe - when searching with ingredientName - then respond with successful response'(String ingredient) {
        given:
            setupRecipes()

        when:
            var searchRequest = prepareSearchRequestWithIngredientName(ingredient)
            def searchBody = objectMapper.writeValueAsString(searchRequest)
            var responseEntity = api.searchRecipe(searchBody)
            var response = responseEntity.body

        then:
            response.size() == 1
            response.first().name == existingRecipe.name
            response.first().id == existingRecipe.id
            response.first().servings == existingRecipe.servings
            response.first().ingredients == existingRecipe.ingredients
            response.first().instructions == existingRecipe.instructions
            response.first().author == existingRecipe.author
        where:
            ingredient << ["Ing1", "Ing2", "Ing3"]
    }

    @Unroll
    def 'given recipe - when searching with recipe Name and ingredient Name - then respond with successful response'(){
        given:
            setupRecipes()

        when:
            var searchRequest = prepareSearchRequestWithIngredientNameAndRecipeName(ingredient, recipe, SearchOperator.AND)
            def searchBody = objectMapper.writeValueAsString(searchRequest)
            var responseEntity = api.searchRecipe(searchBody)
            var response = responseEntity.body

        then:
            response.size() == 1
            response.first().name == existingRecipe.name
            response.first().id == existingRecipe.id
            response.first().servings == existingRecipe.servings
            response.first().ingredients == existingRecipe.ingredients
            response.first().instructions == existingRecipe.instructions
            response.first().author == existingRecipe.author

        where:
            recipe                  | ingredient
            'test-recipe-search'    |  'Ing1'
            'test-recipe-search'    |  'Ing2'
            'test-recipe-search'    |  'Ing3'
    }

    @Unroll
    def 'given recipe - when searching with recipe Name and contains instructions - then respond with successful response'(){
        given:
            setupRecipes()

        when:
            var searchRequest = prepareSearchRequestWithInstructionAndRecipeName(instruction, recipe, SearchOperator.AND)
            def searchBody = objectMapper.writeValueAsString(searchRequest)
            var responseEntity = api.searchRecipe(searchBody)
            var response = responseEntity.body

        then:
            response.size() == size
            response.first().name == existingRecipe.name
            response.first().id == existingRecipe.id
            response.first().servings == existingRecipe.servings
            response.first().ingredients == existingRecipe.ingredients
            response.first().instructions == existingRecipe.instructions
            response.first().author == existingRecipe.author

        where:
            recipe                    | instruction             || size
            'test-recipe-search'    |  "Instruction"            ||  1
            'test-recipe-search'    |  "for you to help"        ||  1
    }

    @Unroll
    def 'given recipe - when searching with recipe type - then respond with successful response'(){
        given:
            setupRecipes()

        when:
            var searchRequest = prepareSearchRequestWithType(RecipeType.VEGETARIAN, queryOperator )
            def searchBody = objectMapper.writeValueAsString(searchRequest)
            var responseEntity = api.searchRecipe(searchBody)
            var response = responseEntity.body

        then:
            response.size() == size
            if(response.size() == 1) {
                assert response.first().name == existingRecipe.name
                assert response.first().id == existingRecipe.id
                assert response.first().servings == existingRecipe.servings
                assert response.first().ingredients == existingRecipe.ingredients
                assert response.first().instructions == existingRecipe.instructions
                assert response.first().author == existingRecipe.author
            }
        where:
                    queryOperator               || size
                    QueryOperator.EQUALS        || 1
    }

    @Unroll
    def 'given recipe - when searching with serving quantity - then respond with successful response'(){
        given:
            setupRecipes()
        when:
            var searchRequest = prepareSearchRequestWithServings(servings, queryOperator )
            def searchBody = objectMapper.writeValueAsString(searchRequest)
            var responseEntity = api.searchRecipe(searchBody)
            var response = responseEntity.body

        then:
            response.size() == size
            if(response.size() == 1) {
                assert response.first().name == existingRecipe.name
                assert response.first().id == existingRecipe.id
                assert response.first().servings == existingRecipe.servings
                assert response.first().ingredients == existingRecipe.ingredients
                assert response.first().instructions == existingRecipe.instructions
                assert response.first().author == existingRecipe.author
            }
        where:
            servings |  queryOperator               || size
            50       |  QueryOperator.EQUALS        || 1
            49       | QueryOperator.GREATER_THAN   || 1
            51       | QueryOperator.GREATER_THAN   || 0
            0       |  QueryOperator.LESS_THAN      || 0
    }

    void setupRecipes() {
        if(!dataSetup) {
            def recipeName = 'test-recipe-search'
            def request = dataStub.getCreateRecipeRequest(RecipeType.VEGETARIAN)
            request.instructions  =  "Instruction testing here for you to help testing"
            request.name = recipeName
            request.ingredients =  List.of("Ing1", "Ing2", "Ing3")
            request.servings = 50
            def body = objectMapper.writeValueAsString(request)
            var responseEntity = api.addRecipe(body)
            assert responseEntity.statusCode.'2xxSuccessful'
            existingRecipe = responseEntity.body
            dataSetup = true
        }
    }
}
