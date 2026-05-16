package com.eventmaster.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayRoutesConfig {

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Value("${services.event-service.url}")
    private String eventServiceUrl;

    @Value("${services.recommendation-service.url}")
    private String recommendationServiceUrl;

    @Value("${services.feed-service.url}")
    private String feedServiceUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Saved-events lives in event-service even though the path starts with /users.
                // This more-specific route must come before the generic /users/** route.
                .route("saved-events", r -> r
                        .path("/users/*/saved-events")
                        .and().method(HttpMethod.GET)
                        .filters(f -> f.rewritePath("/users/(?<username>[^/]+)/saved-events", "/event-service/users/${username}/saved-events"))
                        .uri(eventServiceUrl))
                // /users → user-service:8080/user-service/users
                .route("user-service", r -> r
                        .path("/users", "/users/**")
                        .filters(f -> f.rewritePath("/users(?<segment>/?.*)", "/user-service/users${segment}"))
                        .uri(userServiceUrl))
                // /events → event-service:8081/event-service/events
                .route("event-service", r -> r
                        .path("/events", "/events/**")
                        .filters(f -> f.rewritePath("/events(?<segment>/?.*)", "/event-service/events${segment}"))
                        .uri(eventServiceUrl))
                // /recommendations → recommendation-service:8082/recommendation-service/recommendations
                .route("recommendation-service", r -> r
                        .path("/recommendations", "/recommendations/**")
                        .filters(f -> f.rewritePath("/recommendations(?<segment>/?.*)", "/recommendation-service/recommendations${segment}"))
                        .uri(recommendationServiceUrl))
                // /feed → feed-service:8083/feed-service/feed
                .route("feed-service", r -> r
                        .path("/feed", "/feed/**")
                        .filters(f -> f.rewritePath("/feed(?<segment>/?.*)", "/feed-service/feed${segment}"))
                        .uri(feedServiceUrl))
                .build();
    }
}
