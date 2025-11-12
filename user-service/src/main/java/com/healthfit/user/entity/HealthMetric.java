package com.healthfit.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Health Metric Entity - Track weight, BMI, measurements over time
 */
@Entity
@Table(name = "health_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @Column(nullable = false)
    private Double weight; // in kg

    private Double height; // in cm

    private Double bmi;

    @Column(name = "body_fat_percentage")
    private Double bodyFatPercentage;

    @Column(name = "muscle_mass")
    private Double muscleMass; // in kg

    @Column(name = "waist_circumference")
    private Double waistCircumference; // in cm

    @Column(name = "chest_circumference")
    private Double chestCircumference; // in cm

    @Column(name = "hip_circumference")
    private Double hipCircumference; // in cm

    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordedDate == null) {
            recordedDate = LocalDate.now();
        }
        calculateBmi();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateBmi();
    }

    /**
     * Calculate BMI if weight and height are available
     */
    private void calculateBmi() {
        if (weight != null && height != null && height > 0) {
            double heightInMeters = height / 100.0;
            this.bmi = weight / (heightInMeters * heightInMeters);
        }
    }
}
