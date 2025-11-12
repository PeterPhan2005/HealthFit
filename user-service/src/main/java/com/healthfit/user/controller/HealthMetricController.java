package com.healthfit.user.controller;

import com.healthfit.common.dto.ApiResponse;
import com.healthfit.user.dto.HealthMetricRequest;
import com.healthfit.user.entity.HealthMetric;
import com.healthfit.user.entity.User;
import com.healthfit.user.service.HealthMetricService;
import com.healthfit.user.service.UserService;
import com.healthfit.user.service.UserSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Health Metric Controller
 * Manages health metrics for authenticated users
 */
@RestController
@RequestMapping("/api/users/{userId}/metrics")
@RequiredArgsConstructor
@Slf4j
public class HealthMetricController {

    private final HealthMetricService healthMetricService;
    private final UserService userService;
    private final UserSyncService userSyncService;

    /**
     * Log a new health metric
     * POST /api/users/{userId}/metrics
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HealthMetric>> logMetric(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id") String authenticatedUserId,
            @Valid @RequestBody HealthMetricRequest request) {
        log.info("Logging health metric for userId: {}", userId);
        
        // Verify user can only access their own data
        Long authUserId = userService.getUserIdFromHeader(authenticatedUserId);
        userService.verifyUserAccess(userId, authUserId);
        
        // Ensure user exists in User Service (sync from Auth if needed)
        User user = userSyncService.getOrSyncUser(userId);
        log.debug("User verified: {}", user.getEmail());
        
        HealthMetric metric = healthMetricService.logMetric(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Health metric logged successfully", metric));
    }

    /**
     * Get all metrics for a user
     * GET /api/users/{userId}/metrics
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HealthMetric>>> getMetrics(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id") String authenticatedUserId) {
        log.info("Fetching all metrics for userId: {}", userId);
        
        Long authUserId = userService.getUserIdFromHeader(authenticatedUserId);
        userService.verifyUserAccess(userId, authUserId);
        
        List<HealthMetric> metrics = healthMetricService.getMetricHistory(userId);
        return ResponseEntity.ok(ApiResponse.success("Metrics retrieved successfully", metrics));
    }

    /**
     * Get metrics within date range
     * GET /api/users/{userId}/metrics?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping(params = {"startDate", "endDate"})
    public ResponseEntity<ApiResponse<List<HealthMetric>>> getMetricsByDateRange(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id") String authenticatedUserId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching metrics for userId: {} from {} to {}", userId, startDate, endDate);
        
        Long authUserId = userService.getUserIdFromHeader(authenticatedUserId);
        userService.verifyUserAccess(userId, authUserId);
        
        List<HealthMetric> metrics = healthMetricService.getMetricsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Metrics retrieved successfully", metrics));
    }

    /**
     * Get latest metric for a user
     * GET /api/users/{userId}/metrics/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<HealthMetric>> getLatestMetric(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id") String authenticatedUserId) {
        log.info("Fetching latest metric for userId: {}", userId);
        
        Long authUserId = userService.getUserIdFromHeader(authenticatedUserId);
        userService.verifyUserAccess(userId, authUserId);
        
        Optional<HealthMetric> metric = healthMetricService.getLatestMetric(userId);
        return metric.map(m -> ResponseEntity.ok(ApiResponse.success("Latest metric found", m)))
                .orElse(ResponseEntity.ok(ApiResponse.success("No metrics found", null)));
    }

    /**
     * Delete a metric
     * DELETE /api/users/{userId}/metrics/{metricId}
     */
    @DeleteMapping("/{metricId}")
    public ResponseEntity<ApiResponse<Void>> deleteMetric(
            @PathVariable Long userId,
            @PathVariable Long metricId,
            @RequestHeader(value = "X-User-Id") String authenticatedUserId) {
        log.info("Deleting metric id: {} for userId: {}", metricId, userId);
        
        Long authUserId = userService.getUserIdFromHeader(authenticatedUserId);
        userService.verifyUserAccess(userId, authUserId);
        
        healthMetricService.deleteMetric(userId, metricId);
        return ResponseEntity.ok(ApiResponse.success("Metric deleted successfully", null));
    }
}
