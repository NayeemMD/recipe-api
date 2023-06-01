package com.food.integrationtests

import com.fasterxml.jackson.databind.ObjectMapper
import com.food.integrationtests.config.ApplicationContext
import com.food.integrationtests.config.RestApi
import com.food.integrationtests.v1.request.Author
import com.food.integrationtests.v1.request.RecipeRequest
import com.food.integrationtests.v1.request.RecipeType
import com.food.integrationtests.v1.response.PageRecipeDTO
import com.food.integrationtests.v1.response.RecipeDTO
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
@ContextConfiguration(classes = [ApplicationContext])
class GetAllRecipesSpec extends Specification {

    @Autowired
    RestApi restApi

    @Autowired
    ObjectMapper objectMapper

    @Unroll
    def 'given page details, when fetching page responses, then should respond with successful response'() {
        given:
            def authorRequest = new Author()
            authorRequest.email = authorEmail
            authorRequest.name = author
            def request = getCreateRecipeRequest(authorRequest)
            def requestBody = objectMapper.writeValueAsString(request)

        and:
            var responseEntity1 = restApi.addRecipe(requestBody).block()
            RecipeDTO response = responseEntity1.body
        when:
            def responseEntity = restApi.getPageRecipes(pageNumber, limit).block()
            def stringBody = responseEntity.body
            PageRecipeDTO pageRecipeDTO = objectMapper.readValue(stringBody, PageRecipeDTO);

        then:
            responseEntity.statusCode.is2xxSuccessful()
            pageRecipeDTO.pageNumber                == pageNumber
            pageRecipeDTO.recipes.size()            ==  limit
            pageRecipeDTO.pageSize                  == limit
        and: 'then delete it'
        var deleted =  restApi.deleteRecipe(response.id,
                                            objectMapper.writeValueAsString(authorRequest)).block()
        deleted.statusCode.is2xxSuccessful()

        where:
            author| authorEmail         | pageNumber  | limit
            "a2" | "ab@gmalil.com"     |       0     |   1

    }

    RecipeRequest getCreateRecipeRequest(author) {
        def request = new RecipeRequest()
        request.setAuthor(author)
        request.setName('get-all-recipes')
        request.setServings(10)
        request.setInstructions("instruction for adding a new recipe for you!")
        request.setIngredients(List.of("Ing1", "Ing2", "Ing3"))
        request.setType(RecipeType.NON_VEGETARIAN)
        request
    }

}
