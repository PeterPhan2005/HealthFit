package com.healthfit.auth.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Token Blacklist Service
 * Manages revoked/blacklisted JWT tokens for logout functionality
 * 
 * Note: In production, use Redis for distributed token blacklist
 * For this project, we use in-memory HashSet for simplicity
 */
@Service
public class TokenBlacklistService {

    // In-memory blacklist (use Redis in production)
    private final Set<String> blacklistedTokens = new HashSet<>();

    /**
     * Add token to blacklist (when user logs out)
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    /**
     * Remove token from blacklist (for cleanup after token expires)
     * In production with Redis, set TTL = token expiration time
     */
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }

    /**
     * Clear all blacklisted tokens (for testing/maintenance)
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }

    /**
     * Get blacklist size (for monitoring)
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
}
