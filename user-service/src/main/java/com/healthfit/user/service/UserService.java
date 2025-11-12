package com.healthfit.user.service;

import com.healthfit.user.dto.UpdateProfileRequest;
import com.healthfit.user.entity.User;
import com.healthfit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user profile by ID
     */
    public User getUserById(Long userId) {
        log.info("Fetching user profile for userId: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Get user profile by email
     */
    public User getUserByEmail(String email) {
        log.info("Fetching user profile for email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Update user profile
     */
    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for userId: {}", userId);
        
        User user = getUserById(userId);
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getCurrentWeight() != null) {
            user.setCurrentWeight(request.getCurrentWeight());
        }
        if (request.getCurrentHeight() != null) {
            user.setCurrentHeight(request.getCurrentHeight());
        }
        if (request.getActivityLevel() != null) {
            user.setActivityLevel(request.getActivityLevel());
        }

        // BMI will be auto-calculated by @PreUpdate hook
        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for userId: {}", userId);
        
        return updatedUser;
    }

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Create or sync user from auth-service
     * (Called when user registers or first accesses user-service)
     */
    @Transactional
    public User createUser(String email, String fullName, User.Role role) {
        log.info("Creating user profile for email: {}", email);
        
        if (existsByEmail(email)) {
            log.warn("User already exists with email: {}", email);
            return getUserByEmail(email);
        }

        User user = User.builder()
                .email(email)
                .fullName(fullName)
                .role(role)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User profile created successfully with id: {}", savedUser.getId());
        
        return savedUser;
    }
}
