package com.healthfit.user.service;

import com.healthfit.common.dto.ApiResponse;
import com.healthfit.common.exception.NotFoundException;
import com.healthfit.user.client.AuthServiceClient;
import com.healthfit.user.dto.UserDTO;
import com.healthfit.user.entity.User;
import com.healthfit.user.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for syncing users from Auth Service to User Service
 * Includes Circuit Breaker pattern for resilience
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncService {

    private final AuthServiceClient authServiceClient;
    private final UserRepository userRepository;

    /**
     * Sync user from Auth Service with Circuit Breaker and Retry
     * Called when user first accesses User Service
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "syncUserFallback")
    @Retry(name = "authService")
    @Transactional
    public User syncUserFromAuth(Long userId) {
        log.info("Syncing user {} from Auth Service", userId);
        
        try {
            // Fetch user from Auth Service
            ApiResponse<UserDTO> response = authServiceClient.getUserById(userId);
            
            if (response == null || !response.isSuccess() || response.getData() == null) {
                throw new NotFoundException("User not found in Auth Service: " + userId);
            }
            
            UserDTO userDTO = response.getData();
            
            // Check if user already exists in User Service
            User user = userRepository.findById(userId).orElse(null);
            
            if (user != null) {
                // Update existing user
                log.info("Updating existing user: {}", userDTO.getEmail());
                user.setEmail(userDTO.getEmail());
                user.setFullName(userDTO.getFullName());
                user.setRole(User.Role.valueOf(userDTO.getRole()));
                user.setIsActive(userDTO.getIsActive());
                user.setGender(userDTO.getGender() != null ? User.Gender.valueOf(userDTO.getGender()) : null);
                user.setDateOfBirth(userDTO.getDateOfBirth());
                user.setCurrentWeight(userDTO.getCurrentWeight());
                user.setCurrentHeight(userDTO.getCurrentHeight());
                user.setCurrentBmi(userDTO.getCurrentBmi());
                user.setActivityLevel(userDTO.getActivityLevel() != null ? User.ActivityLevel.valueOf(userDTO.getActivityLevel()) : null);
            } else {
                // Create new user
                log.info("Creating new user from Auth Service: {}", userDTO.getEmail());
                user = userDTO.toEntity();
            }
            
            user = userRepository.save(user);
            log.info("User synced successfully: {}", user.getEmail());
            
            return user;
            
        } catch (Exception e) {
            log.error("Failed to sync user {} from Auth Service: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to sync user from Auth Service: " + e.getMessage(), e);
        }
    }

    /**
     * Fallback method when Auth Service is unavailable
     * Returns existing user from User Service DB or throws exception
     */
    private User syncUserFallback(Long userId, Exception e) {
        log.error("Circuit breaker fallback for user sync: {}. Error: {}", userId, e.getMessage());
        
        // Try to return cached user from User Service DB
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                    "Auth Service is temporarily unavailable and user not found in cache. Please try again later."
                ));
    }

    /**
     * Get or sync user - returns existing user or syncs from Auth Service
     */
    @Transactional
    public User getOrSyncUser(Long userId) {
        // Check if user exists in User Service
        return userRepository.findById(userId)
                .orElseGet(() -> {
                    log.info("User {} not found in User Service, syncing from Auth Service", userId);
                    return syncUserFromAuth(userId);
                });
    }
}
