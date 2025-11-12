package com.healthfit.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthMetricRequest {

    @PastOrPresent(message = "Recorded date cannot be in the future")
    private LocalDate recordedDate;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;

    @Positive(message = "Height must be positive")
    private Double height;

    private Double bodyFatPercentage;

    private Double muscleMass;

    private Double waistCircumference;

    private Double chestCircumference;

    private Double hipCircumference;

    private String notes;
}
