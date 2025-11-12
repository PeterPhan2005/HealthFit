package com.soa.auth.util;

import com.soa.auth.entity.Customer;
import com.soa.auth.repository.CustomerRepository;
import com.soa.auth.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting current customer from Spring Security context.
 */
@Component
public class SecurityUtil {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Get the currently authenticated customer.
     * 
     * @param authentication Spring Security Authentication object
     * @return Customer entity or null if not authenticated
     */
    public Customer getCurrentCustomer(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return customerRepository.findByUsername(userDetails.getUsername()).orElse(null);
        }

        return null;
    }
}
