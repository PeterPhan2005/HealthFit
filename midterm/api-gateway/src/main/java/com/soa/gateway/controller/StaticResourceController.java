package com.soa.gateway.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Controller to serve static HTML files (login.html, home.html)
 * and redirect root URL to login page
 */
@RestController
public class StaticResourceController {

    /**
     * Redirect root URL to login page
     * http://localhost:8080/ -> http://localhost:8080/login.html
     */
    @GetMapping("/")
    public Mono<Void> redirectToLogin(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("/login.html"));
        return exchange.getResponse().setComplete();
    }

    /**
     * Serve login.html
     * Access: http://localhost:8080/login.html
     */
    @GetMapping(value = "/login.html", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<Resource> getLoginPage() {
        return Mono.just(new ClassPathResource("static/login.html"));
    }

    /**
     * Serve home.html
     * Access: http://localhost:8080/home.html
     */
    @GetMapping(value = "/home.html", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<Resource> getHomePage() {
        return Mono.just(new ClassPathResource("static/home.html"));
    }

    /**
     * Health check endpoint
     * Access: http://localhost:8080/health
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        return Mono.just(ResponseEntity.ok("✅ API Gateway is running!\n" +
                "📄 Login: http://localhost:8080/login.html\n" +
                "🏠 Home: http://localhost:8080/home.html"));
    }
}
