package com.healthfit.auth.controller;

import com.healthfit.auth.dto.AuthResponse;
import com.healthfit.auth.dto.LoginRequest;
import com.healthfit.auth.dto.RegisterRequest;
import com.healthfit.auth.entity.User;
import com.healthfit.auth.service.AuthService;
import com.healthfit.auth.service.TokenBlacklistService;
import com.healthfit.common.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.cookie.enabled:true}")
    private boolean cookieEnabled;

    @Value("${jwt.cookie.max-age:86400}")
    private int cookieMaxAge;

    private static final String JWT_COOKIE_NAME = "jwt_token";

    /**
     * Register a new user
     * POST /api/auth/register
     * 
     * Supports both:
     * 1. Token in response body (for mobile apps)
     * 2. Token in HttpOnly cookie (for web browsers) - if jwt.cookie.enabled=true
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        log.info("POST /api/auth/register - Register new user: {}", request.getEmail());
        AuthResponse authResponse = authService.register(request);
        
        // Optionally set JWT in HttpOnly cookie for web browsers
        if (cookieEnabled) {
            setJwtCookie(response, authResponse.getToken());
            log.debug("JWT token set in HttpOnly cookie");
        }
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authResponse));
    }

    /**
     * Login user
     * POST /api/auth/login
     * 
     * Supports both:
     * 1. Token in response body (for mobile apps)
     * 2. Token in HttpOnly cookie (for web browsers) - if jwt.cookie.enabled=true
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        log.info("POST /api/auth/login - Login user: {}", request.getEmail());
        AuthResponse authResponse = authService.login(request);
        
        // Optionally set JWT in HttpOnly cookie for web browsers
        if (cookieEnabled) {
            setJwtCookie(response, authResponse.getToken());
            log.debug("JWT token set in HttpOnly cookie");
        }
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    /**
     * Get current authenticated user
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        log.info("GET /api/auth/me - Get current user: {}", user.getEmail());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Logout user (blacklist token)
     * POST /api/auth/logout
     * 
     * Supports both:
     * 1. Token from Authorization header
     * 2. Token from HttpOnly cookie
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = null;
        
        // Try to get token from Authorization header first (for mobile apps)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        // Fallback to cookie (for web browsers)
        else if (cookieEnabled) {
            token = getJwtFromCookie(request);
        }
        
        if (token != null) {
            tokenBlacklistService.blacklistToken(token);
            
            // Clear cookie if enabled
            if (cookieEnabled) {
                clearJwtCookie(response);
                log.debug("JWT cookie cleared");
            }
            
            log.info("POST /api/auth/logout - Token blacklisted successfully");
            return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
        }
        
        return ResponseEntity.badRequest().body(ApiResponse.error("No valid token found"));
    }

    /**
     * Health check endpoint
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth Service is running"));
    }

    // ==================== Helper Methods ====================

    /**
     * Set JWT token in HttpOnly cookie
     */
    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);      // Prevent XSS attacks - cannot be accessed by JavaScript
        cookie.setSecure(false);       // Set to true in production (HTTPS only) - false for localhost testing
        cookie.setPath("/");           // Cookie available for all paths
        cookie.setMaxAge(cookieMaxAge); // Cookie expiration (same as JWT expiration)
        // cookie.setAttribute("SameSite", "Strict"); // CSRF protection - uncomment for production
        
        response.addCookie(cookie);
    }

    /**
     * Get JWT token from cookie
     */
    private String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Clear JWT cookie (for logout)
     */
    private void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immediately
        
        response.addCookie(cookie);
    }
}
