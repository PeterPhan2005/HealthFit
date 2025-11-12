package com.healthfit.user.service;

import com.healthfit.common.exception.NotFoundException;
import com.healthfit.user.entity.User;
import com.healthfit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Service for managing user profiles
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    /**
     * Get all users (admin only)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Create or update user (for sync from Auth Service)
     */
    @Transactional
    public User saveUser(User user) {
        log.info("Saving user: {}", user.getEmail());
        return userRepository.save(user);
    }

    /**
     * Update user profile
     */
    @Transactional
    public User updateUser(Long id, User userUpdate) {
        User existingUser = getUserById(id);
        
        // Update allowed fields
        if (userUpdate.getFullName() != null) {
            existingUser.setFullName(userUpdate.getFullName());
        }
        if (userUpdate.getGender() != null) {
            existingUser.setGender(userUpdate.getGender());
        }
        if (userUpdate.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userUpdate.getDateOfBirth());
        }
        if (userUpdate.getCurrentWeight() != null) {
            existingUser.setCurrentWeight(userUpdate.getCurrentWeight());
        }
        if (userUpdate.getCurrentHeight() != null) {
            existingUser.setCurrentHeight(userUpdate.getCurrentHeight());
        }
        if (userUpdate.getCurrentBmi() != null) {
            existingUser.setCurrentBmi(userUpdate.getCurrentBmi());
        }
        if (userUpdate.getActivityLevel() != null) {
            existingUser.setActivityLevel(userUpdate.getActivityLevel());
        }
        
        log.info("Updated user: {}", existingUser.getEmail());
        return userRepository.save(existingUser);
    }

    /**
     * Delete user (admin only)
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("Deleted user: {}", user.getEmail());
    }

    /**
     * Check if user exists
     */
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Check if user exists by email
     */
    @Transactional(readOnly = true)
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user ID from Gateway header
     */
    public Long getUserIdFromHeader(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            throw new RuntimeException("X-User-Id header is required");
        }
        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid X-User-Id header format");
        }
    }

    /**
     * Verify user has permission to access resource
     */
    public void verifyUserAccess(Long requestedUserId, Long authenticatedUserId) {
        if (!requestedUserId.equals(authenticatedUserId)) {
            throw new RuntimeException("Access denied: Cannot access another user's data");
        }
    }
}
