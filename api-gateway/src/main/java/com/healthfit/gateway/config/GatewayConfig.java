package com.healthfit.gateway.config;

import com.healthfit.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway routing configuration
 * Defines routes to backend services
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Configuring Gateway Routes...");
        
        return builder.routes()
                // Auth Service Routes (no authentication needed for login/register)
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter) // Apply JWT filter (will skip for public endpoints)
                                .preserveHostHeader()
                                .addRequestHeader("X-Gateway-Route", "auth-service")
                        )
                        .uri("http://localhost:8081")
                )
                
                // User Service Routes (requires authentication)
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter) // JWT authentication required
                                .preserveHostHeader()
                                .addRequestHeader("X-Gateway-Route", "user-service")
                        )
                        .uri("http://localhost:8082")
                )
                
                // Health check route (gateway itself)
                .route("gateway-health", r -> r
                        .path("/gateway/health")
                        .filters(f -> f.setStatus(200))
                        .uri("no://op")
                )
                
                .build();
    }
}
