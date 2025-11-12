package com.healthfit.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for user information to share with other services
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private Boolean isActive;
    private String gender;
    private LocalDate dateOfBirth;
    private Double currentWeight;
    private Double currentHeight;
    private Double currentBmi;
    private String activityLevel;
}
