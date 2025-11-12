package com.healthfit.user.controller;

import com.healthfit.common.dto.ApiResponse;
import com.healthfit.user.dto.HealthMetricRequest;
import com.healthfit.user.entity.HealthMetric;
import com.healthfit.user.service.HealthMetricService;
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

@RestController
@RequestMapping("/api/users/{userId}/metrics")
@RequiredArgsConstructor
@Slf4j
public class HealthMetricController {

    private final HealthMetricService healthMetricService;

    /**
     * Log a new health metric
     * POST /api/users/{userId}/metrics
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HealthMetric>> logMetric(
            @PathVariable Long userId,
            @Valid @RequestBody HealthMetricRequest request) {
        log.info("Logging health metric for userId: {}", userId);
        
        HealthMetric metric = healthMetricService.logMetric(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Health metric logged successfully", metric));
    }

    /**
     * Get all metrics for a user
     * GET /api/users/{userId}/metrics
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<HealthMetric>>> getMetrics(@PathVariable Long userId) {
        log.info("Fetching all metrics for userId: {}", userId);
        
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching metrics for userId: {} from {} to {}", userId, startDate, endDate);
        
        List<HealthMetric> metrics = healthMetricService.getMetricsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Metrics retrieved successfully", metrics));
    }

    /**
     * Get latest metric for a user
     * GET /api/users/{userId}/metrics/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<HealthMetric>> getLatestMetric(@PathVariable Long userId) {
        log.info("Fetching latest metric for userId: {}", userId);
        
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
            @PathVariable Long metricId) {
        log.info("Deleting metric id: {} for userId: {}", metricId, userId);
        
        healthMetricService.deleteMetric(userId, metricId);
        return ResponseEntity.ok(ApiResponse.success("Metric deleted successfully", null));
    }
}
