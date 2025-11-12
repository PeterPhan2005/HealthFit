package com.soa.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // Public endpoints that don't require JWT
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/actuator/health"
    );

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();

            logger.info("=== JWT Filter - Path: {}", path);

            // Allow public endpoints
            if (PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith)) {
                logger.info("=== Public endpoint, bypassing JWT check");
                return chain.filter(exchange);
            }

            // Check for Authorization header first, then Cookie
            String authHeader = request.getHeaders().getFirst("Authorization");
            String token = null;
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                logger.info("=== JWT Token from Authorization header, length: {}", token.length());
            } else {
                // Try to get token from Cookie (auth-service uses "jwt_token" as cookie name)
                String cookieHeader = request.getHeaders().getFirst("Cookie");
                if (cookieHeader != null && cookieHeader.contains("jwt_token=")) {
                    String[] cookies = cookieHeader.split(";");
                    for (String cookie : cookies) {
                        cookie = cookie.trim();
                        if (cookie.startsWith("jwt_token=")) {
                            token = cookie.substring(10); // Remove "jwt_token="
                            logger.info("=== JWT Token from Cookie (jwt_token), length: {}", token.length());
                            break;
                        }
                    }
                }
            }
            
            if (token == null) {
                logger.error("=== ERROR: Missing JWT token (checked both Authorization header and Cookie)");
                return onError(exchange, "Missing or invalid Authorization token", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Validate JWT token - MUST use BASE64 decoding like auth-service!
                Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
                logger.info("=== JWT Secret key created with BASE64 decoding");
                
                Claims claims = Jwts.parser()
                        .verifyWith((SecretKey) key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                logger.info("=== JWT validated successfully for user: {}", claims.getSubject());

                // Add user info to request headers for downstream services
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", claims.getSubject())
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                logger.error("=== ERROR: JWT validation failed: {}", e.getMessage());
                e.printStackTrace();
                return onError(exchange, "Invalid JWT token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}
