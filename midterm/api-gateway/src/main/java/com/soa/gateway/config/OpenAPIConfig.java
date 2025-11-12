package com.soa.gateway.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration for API Gateway
 * 
 * Swagger UI: http://localhost:8080/docs
 * OpenAPI JSON: http://localhost:8080/openapi.json
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI apiGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iBanking API Gateway")
                        .description("API Gateway cho hệ thống thanh toán học phí trực tuyến")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("iBanking Support")
                                .email("support@ibanking.com")
                                .url("http://localhost:8080"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("iBanking Documentation")
                        .url("http://localhost:8080/docs"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .schemaRequirement("Bearer Authentication", new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Token từ /api/auth/login. Format: Bearer <token>"));
    }
}
