package com.healthfit.auth.dto;

import com.healthfit.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String fullName;
        private User.Gender gender;
        private LocalDate dateOfBirth;
        private Integer age;
        private Double currentWeight;
        private Double currentHeight;
        private Double currentBmi;
        private User.ActivityLevel activityLevel;
        private User.Role role;
        private LocalDateTime createdAt;
    }

    public static AuthResponse of(String token, User user, int age) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .gender(user.getGender())
                        .dateOfBirth(user.getDateOfBirth())
                        .age(age)
                        .currentWeight(user.getCurrentWeight())
                        .currentHeight(user.getCurrentHeight())
                        .currentBmi(user.getCurrentBmi())
                        .activityLevel(user.getActivityLevel())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
    }
}
