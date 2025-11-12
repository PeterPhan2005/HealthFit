package com.healthfit.auth.controller;

import com.healthfit.auth.dto.UserDTO;
import com.healthfit.auth.entity.User;
import com.healthfit.auth.repository.UserRepository;
import com.healthfit.common.dto.ApiResponse;
import com.healthfit.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal Controller for inter-service communication
 * These endpoints should NOT be exposed through API Gateway
 * Only for direct service-to-service calls
 */
@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
@Slf4j
public class InternalAuthController {

    private final UserRepository userRepository;

    /**
     * Get user by ID (internal endpoint for User Service)
     * GET /api/auth/internal/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        log.info("Internal API: GET /api/auth/internal/users/{}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
        
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .dateOfBirth(user.getDateOfBirth())
                .currentWeight(user.getCurrentWeight())
                .currentHeight(user.getCurrentHeight())
                .currentBmi(user.getCurrentBmi())
                .activityLevel(user.getActivityLevel() != null ? user.getActivityLevel().name() : null)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success("User retrieved", userDTO));
    }
}
