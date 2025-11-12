package com.healthfit.auth.service;

import com.healthfit.auth.dto.AuthResponse;
import com.healthfit.auth.dto.LoginRequest;
import com.healthfit.auth.dto.RegisterRequest;
import com.healthfit.auth.entity.User;
import com.healthfit.auth.repository.UserRepository;
import com.healthfit.common.exception.BadRequestException;
import com.healthfit.common.exception.UnauthorizedException;
import com.healthfit.common.util.DateUtil;
import com.healthfit.common.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Validate email
        if (!ValidationUtil.isValidEmail(request.getEmail())) {
            throw new BadRequestException("Invalid email format");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Validate password
        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            throw new BadRequestException("Password must be at least 8 characters and contain at least one letter and one number");
        }

        // Validate date of birth
        if (!DateUtil.isValidDateOfBirth(request.getDateOfBirth())) {
            throw new BadRequestException("Invalid date of birth. Age must be between 13 and 120 years");
        }

        // Validate weight
        if (!ValidationUtil.isValidWeight(request.getCurrentWeight())) {
            throw new BadRequestException("Weight must be between 20 and 500 kg");
        }

        // Calculate BMI if height is provided
        Double bmi = null;
        if (request.getCurrentHeight() != null) {
            if (!ValidationUtil.isValidHeight(request.getCurrentHeight())) {
                throw new BadRequestException("Height must be between 50 and 300 cm");
            }
            bmi = ValidationUtil.calculateBMI(request.getCurrentHeight(), request.getCurrentWeight());
        }

        // Create user entity
        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .currentWeight(request.getCurrentWeight())
                .currentHeight(request.getCurrentHeight())
                .currentBmi(bmi)
                .activityLevel(request.getActivityLevel())
                .role(User.Role.USER)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        // Generate JWT token
        String token = jwtService.generateToken(user);
        int age = DateUtil.calculateAge(user.getDateOfBirth());

        return AuthResponse.of(token, user, age);
    }

    /**
     * Login user
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to login user with email: {}", request.getEmail());

        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().toLowerCase().trim(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        // Fetch user
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // Check if account is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        log.info("User logged in successfully with ID: {}", user.getId());

        // Generate JWT token
        String token = jwtService.generateToken(user);
        int age = DateUtil.calculateAge(user.getDateOfBirth());

        return AuthResponse.of(token, user, age);
    }
}
