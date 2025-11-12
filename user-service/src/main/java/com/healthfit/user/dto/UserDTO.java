package com.healthfit.user.dto;

import com.healthfit.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for user information from Auth Service
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

    /**
     * Convert DTO to User entity
     */
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .fullName(this.fullName)
                .role(this.role != null ? User.Role.valueOf(this.role) : User.Role.USER)
                .isActive(this.isActive != null ? this.isActive : true)
                .gender(this.gender != null ? User.Gender.valueOf(this.gender) : null)
                .dateOfBirth(this.dateOfBirth)
                .currentWeight(this.currentWeight)
                .currentHeight(this.currentHeight)
                .currentBmi(this.currentBmi)
                .activityLevel(this.activityLevel != null ? User.ActivityLevel.valueOf(this.activityLevel) : null)
                .build();
    }
}
