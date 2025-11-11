package com.healthfit.common.util;

import com.healthfit.common.exception.BadRequestException;

/**
 * Utility class for validation operations
 */
public class ValidationUtil {

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validate password strength (min 8 chars, at least 1 letter and 1 number)
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$");
    }

    /**
     * Validate height (in cm, range 50-300)
     */
    public static boolean isValidHeight(Double height) {
        return height != null && height >= 50 && height <= 300;
    }

    /**
     * Validate weight (in kg, range 20-500)
     */
    public static boolean isValidWeight(Double weight) {
        return weight != null && weight >= 20 && weight <= 500;
    }

    /**
     * Validate BMI (range 10-100)
     */
    public static boolean isValidBMI(Double bmi) {
        return bmi != null && bmi >= 10 && bmi <= 100;
    }

    /**
     * Calculate BMI from height and weight
     */
    public static double calculateBMI(double heightInCm, double weightInKg) {
        if (heightInCm <= 0 || weightInKg <= 0) {
            throw new BadRequestException("Height and weight must be positive values");
        }
        double heightInMeters = heightInCm / 100.0;
        return weightInKg / (heightInMeters * heightInMeters);
    }

    /**
     * Validate that a value is positive
     */
    public static void requirePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new BadRequestException(fieldName + " must be a positive value");
        }
    }

    /**
     * Validate that a string is not blank
     */
    public static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " cannot be blank");
        }
    }
}
