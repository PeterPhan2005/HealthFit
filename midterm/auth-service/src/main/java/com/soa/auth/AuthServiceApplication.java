package com.soa.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Authentication Service - Handles login, JWT generation and validation
 * Port: 8081
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("\n" +
                "========================================\n" +
                "   AUTH-SERVICE Started Successfully    \n" +
                "   Port: 8081                           \n" +
                "   Swagger: http://localhost:8081/docs  \n" +
                "========================================\n");
    }
}
