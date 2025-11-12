package com.healthfit.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter to log requests and ensure JWT cookies are forwarded
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        log.info("Gateway Request: {} {} from {}", 
                request.getMethod(), 
                request.getPath(), 
                request.getRemoteAddress());

        // Log cookies (especially JWT)
        HttpCookie jwtCookie = request.getCookies().getFirst("jwt");
        if (jwtCookie != null) {
            log.debug("JWT Cookie present: {}", jwtCookie.getValue().substring(0, Math.min(20, jwtCookie.getValue().length())) + "...");
        }

        // Log Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            log.debug("Authorization header present: {}", authHeader.substring(0, Math.min(30, authHeader.length())) + "...");
        }

        // Continue filter chain
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("Gateway Response: {} with status {}", 
                    request.getPath(), 
                    exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}
