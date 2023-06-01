package com.food.integrationtests

import com.food.integrationtests.config.ApplicationContext
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

@Slf4j
@ContextConfiguration(classes = [ApplicationContext])
class ActuatorSpec extends Specification {

    @Autowired
    WebClient recipeRestServiceClient

    @Autowired
    ObjectMapper objectMapper

    private static final String ACTUATOR_HEALTH_PATH = '/actuator/health'

    def 'actuator/health should be accessible'() {
        when:
            WebClient.ResponseSpec responseSpec = recipeRestServiceClient.get()
                    .uri(ACTUATOR_HEALTH_PATH)
                    .retrieve()
            ResponseEntity<String> response = responseSpec.toEntity(String.class).block()
            Map body = objectMapper.readValue(response.getBody(), Map.class)
        then:
            response.statusCode == HttpStatus.OK
            body.get("status") == "UP"
    }
}
