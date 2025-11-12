package com.healthfit.user.dto;

import com.healthfit.user.entity.Goal;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {

    @NotNull(message = "Goal type is required")
    private Goal.GoalType goalType;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Positive(message = "Target value must be positive")
    private Double targetValue;

    private Double currentValue;

    private String unit;

    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @Future(message = "Target date must be in the future")
    private LocalDate targetDate;
}
