package com.healthfit.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Goal Entity - Track fitness and health goals
 */
@Entity
@Table(name = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false, length = 30)
    private GoalType goalType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "current_value")
    private Double currentValue;

    @Column(length = 20)
    private String unit; // kg, cm, calories, minutes, etc.

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (status == null) {
            status = GoalStatus.IN_PROGRESS;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateStatus();
    }

    /**
     * Update goal status based on current and target values
     */
    private void updateStatus() {
        if (status == GoalStatus.COMPLETED || status == GoalStatus.CANCELLED) {
            return; // Don't auto-update if manually set
        }

        if (targetValue != null && currentValue != null) {
            if (currentValue >= targetValue) {
                status = GoalStatus.COMPLETED;
                if (completedDate == null) {
                    completedDate = LocalDate.now();
                }
            }
        }

        // Check if target date passed
        if (targetDate != null && LocalDate.now().isAfter(targetDate) && status != GoalStatus.COMPLETED) {
            status = GoalStatus.OVERDUE;
        }
    }

    /**
     * Calculate progress percentage
     */
    public Double getProgress() {
        if (targetValue == null || currentValue == null || targetValue == 0) {
            return 0.0;
        }
        return Math.min((currentValue / targetValue) * 100, 100.0);
    }

    public enum GoalType {
        WEIGHT_LOSS,
        WEIGHT_GAIN,
        MUSCLE_GAIN,
        FITNESS_LEVEL,
        CALORIE_INTAKE,
        WORKOUT_FREQUENCY,
        CUSTOM
    }

    public enum GoalStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        OVERDUE,
        CANCELLED
    }
}
