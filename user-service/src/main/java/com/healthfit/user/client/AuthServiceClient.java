package com.healthfit.user.client;

import com.healthfit.common.dto.ApiResponse;
import com.healthfit.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for Auth Service
 * Used to fetch user data from Auth Service and sync to User Service
 */
@FeignClient(
    name = "auth-service",
    url = "${auth-service.url:http://localhost:8081}"
)
public interface AuthServiceClient {

    /**
     * Get user by ID from Auth Service
     * This endpoint should be internal - only called by other services
     */
    @GetMapping("/api/auth/internal/users/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") Long id);
}
