package com.triphub.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator tripHubRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/auth/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/api/events/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8082"))
                .route(r -> r.path("/api/bookings/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8083"))
                .route(r -> r.path("/api/payments/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8084"))
                .route(r -> r.path("/api/tickets/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("http://localhost:8085"))
                .build();
    }
}
