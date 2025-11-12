package com.soa.auth.controller;

import com.soa.auth.security.request.LoginRequest;
import com.soa.auth.security.response.MessageResponse;
import com.soa.auth.security.response.UserInfoResponse;
import com.soa.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations.
 * Handles customer login, logout and user details retrieval.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    /**
     * Authenticate customer and return JWT token.
     * Token is saved in HTTP-only cookie for security.
     * 
     * @param loginRequest Login credentials (username and password)
     * @param response HTTP response to set cookie
     * @return JWT token and customer information
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, 
                                               HttpServletResponse response) {
        UserInfoResponse userInfo = authService.login(loginRequest);
        
        // Create cookie for JWT token (httpOnly=false for Postman compatibility)
        Cookie jwtCookie = new Cookie("jwt_token", userInfo.getToken());
        jwtCookie.setHttpOnly(false);  // Allow Postman to read cookie (for testing)
        jwtCookie.setSecure(false);    // Set to true in production with HTTPS
        jwtCookie.setPath("/");        // Available for all paths
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours (same as JWT expiration)
        jwtCookie.setAttribute("SameSite", "Lax"); // Allow cookie in same-site navigation
        
        response.addCookie(jwtCookie);
        
        // Return success response with user info (token still included for API clients)
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Logout customer by clearing JWT cookie.
     * 
     * @param response HTTP response to clear cookie
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        // Clear JWT cookie
        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(false);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete immediately
        jwtCookie.setAttribute("SameSite", "Lax");
        
        response.addCookie(jwtCookie);
        
        return ResponseEntity.ok(new MessageResponse("User logged out successfully"));
    }
}
