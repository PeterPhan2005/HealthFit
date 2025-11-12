package com.soa.otp.util;

import com.soa.otp.security.services.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

/**
 * Utility class for extracting current customer ID from Spring Security context.
 * In microservices, we only need the ID - no entity dependencies.
 */
public class SecurityUtil {

    /**
     * Get the currently authenticated customer ID from JWT token.
     * @return Customer ID or null if not authenticated
     */
    public static Long getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return userDetails.getId();
        }

        return null;
    }

    /**
     * Get the currently authenticated username.
     * @return Username or null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return userDetails.getUsername();
        }

        return null;
    }
}
