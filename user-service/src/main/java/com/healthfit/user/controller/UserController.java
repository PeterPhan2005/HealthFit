package com.healthfit.user.controller;

import com.healthfit.common.dto.ApiResponse;
import com.healthfit.user.dto.UpdateProfileRequest;
import com.healthfit.user.entity.User;
import com.healthfit.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Getting profile for authenticated user: {}", userDetails.getUsername());
        
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
    }

    /**
     * Get user profile by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        log.info("Getting profile for userId: {}", id);
        
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User found", user));
    }

    /**
     * Update user profile
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", userDetails.getUsername());
        
        User user = userService.getUserByEmail(userDetails.getUsername());
        User updatedUser = userService.updateProfile(user.getId(), request);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }

    /**
     * Update user profile by ID (for admin or specific use)
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUserById(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Updating profile for userId: {}", id);
        
        User updatedUser = userService.updateProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }
}
