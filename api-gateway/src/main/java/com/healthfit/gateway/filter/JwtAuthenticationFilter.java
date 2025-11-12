package com.healthfit.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * JWT Authentication Filter for API Gateway
 * Validates JWT tokens and adds user information to request headers
 * Based on midterm project architecture - parses JWT directly without calling Auth Service
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GatewayFilter, Ordered {

    private static final String JWT_COOKIE_NAME = "jwt_token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/actuator/health"
    );

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        // Decode BASE64 encoded secret (same as midterm pattern)
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // Generate or extract correlation ID for request tracing
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = java.util.UUID.randomUUID().toString();
        }
        final String finalCorrelationId = correlationId;

        log.debug("[{}] Processing request: {} {}", finalCorrelationId, request.getMethod(), path);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            log.debug("[{}] Public endpoint, skipping authentication: {}", finalCorrelationId, path);
            // Add correlation ID to public endpoints too
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(CORRELATION_ID_HEADER, finalCorrelationId)
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        // Extract JWT token from Authorization header or Cookie
        String token = extractToken(exchange);

        if (token == null) {
            log.warn("[{}] No JWT token found in request to: {}", finalCorrelationId, path);
            return onError(exchange, "Authentication required", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Parse and validate JWT token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extract user information from claims
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            log.debug("[{}] JWT validated for user: {} ({})", finalCorrelationId, email, userId);

            // Add user information and correlation ID to request headers for downstream services
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(USER_ID_HEADER, userId)
                    .header(USER_EMAIL_HEADER, email)
                    .header(USER_ROLE_HEADER, role)
                    .header(CORRELATION_ID_HEADER, finalCorrelationId)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("[{}] JWT validation failed: {}", finalCorrelationId, e.getMessage());
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Extract JWT token from Authorization header or Cookie
     */
    private String extractToken(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        // Try Authorization header first (for mobile apps and Postman)
        List<String> authHeaders = request.getHeaders().get(AUTHORIZATION_HEADER);
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith(BEARER_PREFIX)) {
                return authHeader.substring(BEARER_PREFIX.length());
            }
        }

        // Fallback to Cookie (for web browsers)
        HttpCookie cookie = request.getCookies().getFirst(JWT_COOKIE_NAME);
        if (cookie != null) {
            return cookie.getValue();
        }

        return null;
    }

    /**
     * Check if endpoint is public (doesn't require authentication)
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Return error response
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorResponse = String.format(
                "{\"success\":false,\"message\":\"%s\",\"data\":null}",
                message
        );
        
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(errorResponse.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -100; // Execute before other filters
    }
}
