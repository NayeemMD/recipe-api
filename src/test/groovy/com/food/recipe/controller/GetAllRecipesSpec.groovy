package com.food.recipe.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.food.recipe.config.RestAPI
import com.food.recipe.dto.v1.response.RecipeDTO
import com.food.recipe.model.RecipeType
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
class GetAllRecipesSpec extends Specification{

    @Autowired
    private TestRestTemplate restTemplate

    @Autowired
    private DataUtil dataUtil

    @LocalServerPort
    private Integer port = 0
    @Shared
    boolean dataSetup = false
    @Shared
    List<RecipeDTO> responses = new ArrayList<>()

    RestAPI api
    RequestDataStub dataStub
    ObjectMapper objectMapper

    int totalRecords = 10

    def setup() {
        api = new RestAPI(restTemplate, port)
        dataStub = new RequestDataStub()
        objectMapper = new ObjectMapper()
    }

    @Unroll
    def 'given page details, when fetching page responses, then should respond with successful response'() {
        given:
           setupRecipes(totalRecords)
        when:
            def responseEntity = api.getPageRecipes(pageNumber, limit)
            def pageRecipeDTO = responseEntity.body

        then:
            responseEntity.statusCode.value()       == 200
            pageRecipeDTO.pageNumber                == pageNumber
            pageRecipeDTO.recipes.size()            ==  limit
            pageRecipeDTO.pageSize                  == limit

        where:
            pageNumber  | limit
            0           |   2
            1           |   2
            2           |   2
            3           |   2
            4           |   2
    }

    void setupRecipes(int total) {
        if(!dataSetup) {
            for (int i = 0; i < total; i++) {
                def request = dataStub.getCreateRecipeRequest(RecipeType.NON_VEGETARIAN)
                def body = objectMapper.writeValueAsString(request)
                var responseEntity = api.addRecipe(body)
                assert responseEntity.statusCode.'2xxSuccessful'
                responses.add(responseEntity.body)
            }
            dataSetup = true
        }
    }
}
