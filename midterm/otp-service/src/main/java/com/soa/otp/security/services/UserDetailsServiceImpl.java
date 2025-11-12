package com.soa.otp.security.services;

import com.soa.otp.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service to load user details from JWT token.
 * In OTP-SERVICE, we don't have Customer entity, just validate JWT.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In microservices, we just create minimal UserDetails from username
        // The JWT token is already validated by JwtUtils
        
        return new UserDetailsImpl(
                null,  // ID will be extracted from JWT claims if needed
                username,
                username,  // fullName = username by default
                username + "@example.com",  // dummy email
                "",  // no password needed
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
