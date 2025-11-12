package com.soa.gateway.config;

import com.soa.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${CUSTOMER_SERVICE_URL:http://localhost:8082}")
    private String customerServiceUrl;

    @Value("${STUDENT_SERVICE_URL:http://localhost:8083}")
    private String studentServiceUrl;

    @Value("${PAYMENT_SERVICE_URL:http://localhost:8084}")
    private String paymentServiceUrl;

    @Value("${OTP_SERVICE_URL:http://localhost:8085}")
    private String otpServiceUrl;

    @Value("${NOTIFICATION_SERVICE_URL:http://localhost:8086}")
    private String notificationServiceUrl;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service - No JWT required
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri(authServiceUrl))

                // Customer Service - JWT required
                .route("customer-service", r -> r.path("/api/customers/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(customerServiceUrl))

                // Student Service - JWT required
                .route("student-service", r -> r.path("/api/students/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(studentServiceUrl))

                // Payment Service - JWT required
                .route("payment-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(paymentServiceUrl))

                // OTP Service - JWT required
                .route("otp-service", r -> r.path("/api/otp/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(otpServiceUrl))

                // Notification Service - JWT required
                .route("notification-service", r -> r.path("/api/notifications/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri(notificationServiceUrl))

                .build();
    }
}
