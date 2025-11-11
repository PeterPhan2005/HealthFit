package com.healthfit.common.util;

import java.time.LocalDate;
import java.time.Period;

/**
 * Utility class for date operations
 */
public class DateUtil {

    /**
     * Calculate age from date of birth
     */
    public static int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Check if date is in the future
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Check if date is in the past
     */
    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    /**
     * Validate date of birth (not in future, reasonable age range)
     */
    public static boolean isValidDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null || isFutureDate(dateOfBirth)) {
            return false;
        }
        int age = calculateAge(dateOfBirth);
        return age >= 13 && age <= 120; // Reasonable age range
    }
}
