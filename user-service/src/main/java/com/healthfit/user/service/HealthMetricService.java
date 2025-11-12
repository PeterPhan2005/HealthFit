package com.healthfit.user.service;

import com.healthfit.user.dto.HealthMetricRequest;
import com.healthfit.user.entity.HealthMetric;
import com.healthfit.user.repository.HealthMetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthMetricService {

    private final HealthMetricRepository healthMetricRepository;

    /**
     * Log a new health metric
     */
    @Transactional
    public HealthMetric logMetric(Long userId, HealthMetricRequest request) {
        log.info("Logging health metric for userId: {}", userId);

        HealthMetric metric = HealthMetric.builder()
                .userId(userId)
                .recordedDate(request.getRecordedDate() != null ? request.getRecordedDate() : LocalDate.now())
                .weight(request.getWeight())
                .height(request.getHeight())
                .bodyFatPercentage(request.getBodyFatPercentage())
                .muscleMass(request.getMuscleMass())
                .waistCircumference(request.getWaistCircumference())
                .chestCircumference(request.getChestCircumference())
                .hipCircumference(request.getHipCircumference())
                .notes(request.getNotes())
                .build();

        // BMI will be auto-calculated by @PrePersist hook
        HealthMetric savedMetric = healthMetricRepository.save(metric);
        log.info("Health metric logged successfully with id: {}", savedMetric.getId());

        return savedMetric;
    }

    /**
     * Get metric history for a user (ordered by date descending)
     */
    public List<HealthMetric> getMetricHistory(Long userId) {
        log.info("Fetching metric history for userId: {}", userId);
        return healthMetricRepository.findByUserIdOrderByRecordedDateDesc(userId);
    }

    /**
     * Get metrics within a date range
     */
    public List<HealthMetric> getMetricsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching metrics for userId: {} from {} to {}", userId, startDate, endDate);
        return healthMetricRepository.findByUserIdAndRecordedDateBetweenOrderByRecordedDateDesc(userId, startDate, endDate);
    }

    /**
     * Get the latest metric for a user
     */
    public Optional<HealthMetric> getLatestMetric(Long userId) {
        log.info("Fetching latest metric for userId: {}", userId);
        return healthMetricRepository.findTopByUserIdOrderByRecordedDateDesc(userId);
    }

    /**
     * Get metric by ID
     */
    public HealthMetric getMetricById(Long metricId) {
        log.info("Fetching metric by id: {}", metricId);
        return healthMetricRepository.findById(metricId)
                .orElseThrow(() -> new RuntimeException("Health metric not found with id: " + metricId));
    }

    /**
     * Delete a metric
     */
    @Transactional
    public void deleteMetric(Long userId, Long metricId) {
        log.info("Deleting metric id: {} for userId: {}", metricId, userId);
        
        HealthMetric metric = getMetricById(metricId);
        if (!metric.getUserId().equals(userId)) {
            throw new RuntimeException("Metric does not belong to user");
        }

        healthMetricRepository.delete(metric);
        log.info("Metric deleted successfully");
    }
}
