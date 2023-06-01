package com.food.integrationtests.config

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.junit.platform.commons.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

import java.time.Duration

@Slf4j
@Configuration
class ApplicationContext {

    @Configuration
    @Profile('default')
    @PropertySource('application.properties')
    static class DefaultPropertySourceContext {

    }

    @Autowired
    Environment environment

    public static final String API_RECIPE_BASE_URL = 'api.recipe.base.url'
    public static final String LOCAL_ENV = "local"
    public static final String DEV_ENV = "dev"
    public static final String QA_ENV = "qa"
    public static final String LIVE_ENV = "live"

    /**
     * return [local, dev, qa, live] according of the current implementation for this test suite (spring profile) <br/>
     * default value is "local" <br/>
     *
     * @return [local , dev, qa, live]
     */
    static String getCurrentEnv() {

        def env = System.getenv("SPRING_PROFILES_ACTIVE")
        if(!env) {
            log.warn("no env specified, use ${env}")
            return LOCAL_ENV
        }

        env = env.trim().toLowerCase()
        if (![LOCAL_ENV, DEV_ENV, QA_ENV, LIVE_ENV].contains(env)) {
            log.warn("${env} is not a valid env, use ${LOCAL_ENV} instead")
            return LOCAL_ENV
        }

        log.info("use specified env:${env}")
        return env
    }

    @Bean
    WebClient recipeRestServiceClient() {

        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(30000)).wiretap(false)
        WebClient webClient = WebClient.builder()
                .defaultHeaders(httpHeaders -> {
                        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    })
                .baseUrl(environment.getProperty(API_RECIPE_BASE_URL))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build()

        return webClient
    }

    @Bean
    RestApi restApi(WebClient recipeRestServiceClient) {
        new RestApi(recipeRestServiceClient)
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
    }
}
