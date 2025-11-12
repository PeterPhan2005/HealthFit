package com.healthfit.user.controller;

import com.healthfit.common.dto.ApiResponse;
import com.healthfit.user.entity.User;
import com.healthfit.user.service.UserService;
import com.healthfit.user.service.UserSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller
 * Manages user profile operations
 * Gateway provides X-User-Id, X-User-Email headers after JWT validation
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserSyncService userSyncService;

    /**
     * Get current user profile
     * GET /api/users/me
     * Auto-syncs user from Auth Service if not exists
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(
            @RequestHeader(value = "X-User-Id") String userIdHeader) {
        
        Long userId = userService.getUserIdFromHeader(userIdHeader);
        log.info("GET /api/users/me - userId: {}", userId);
        
        // Get or sync user from Auth Service
        User user = userSyncService.getOrSyncUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", user));
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id") String userIdHeader) {
        
        Long authenticatedUserId = userService.getUserIdFromHeader(userIdHeader);
        log.info("GET /api/users/{} - Requested by user: {}", id, authenticatedUserId);
        
        // Users can only access their own profile (unless admin - TODO: implement role check)
        userService.verifyUserAccess(id, authenticatedUserId);
        
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved", user));
    }

    /**
     * Get all users (admin only - TODO: add role check)
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @RequestHeader(value = "X-User-Id") String userIdHeader,
            @RequestHeader(value = "X-User-Role") String userRole) {
        
        Long userId = userService.getUserIdFromHeader(userIdHeader);
        log.info("GET /api/users - Requested by user: {} with role: {}", userId, userRole);
        
        // TODO: Check if user has ADMIN role
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    /**
     * Update user profile
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody User userUpdate,
            @RequestHeader(value = "X-User-Id") String userIdHeader) {
        
        Long authenticatedUserId = userService.getUserIdFromHeader(userIdHeader);
        log.info("PUT /api/users/{} - Updated by user: {}", id, authenticatedUserId);
        
        // Users can only update their own profile
        userService.verifyUserAccess(id, authenticatedUserId);
        
        User updatedUser = userService.updateUser(id, userUpdate);
        return ResponseEntity.ok(ApiResponse.success("User updated", updatedUser));
    }

    /**
     * Delete user (admin only - TODO: add role check)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id") String userIdHeader,
            @RequestHeader(value = "X-User-Role") String userRole) {
        
        Long userId = userService.getUserIdFromHeader(userIdHeader);
        log.info("DELETE /api/users/{} - Requested by user: {} with role: {}", id, userId, userRole);
        
        // TODO: Check if user has ADMIN role
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted"));
    }
}
