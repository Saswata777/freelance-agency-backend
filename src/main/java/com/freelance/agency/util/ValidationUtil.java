package com.freelance.agency.util;

import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {

    /**
     * Check if string is null or blank
     */
    public boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Basic email format validation
     */
    public boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Check if input looks like spam
     * (too short, random characters, repeated chars)
     */
    public boolean isSpam(String input) {
        if (isBlank(input)) return true;

        // Too short
        if (input.trim().length() < 5) return true;

        // All same character
        String trimmed = input.trim();
        boolean allSame = trimmed.chars()
                .distinct()
                .count() == 1;
        if (allSame) return true;

        // Only numbers
        if (trimmed.matches("^[0-9]+$")) return true;

        // Only special characters
        if (trimmed.matches("^[^a-zA-Z0-9]+$")) return true;

        return false;
    }

    /**
     * Sanitize input — remove HTML tags to prevent XSS
     */
    public String sanitize(String input) {
        if (isBlank(input)) return "";
        return input
                .replaceAll("<[^>]*>", "")  // remove HTML tags
                .replaceAll("[<>\"']", "")  // remove dangerous chars
                .trim();
    }

    /**
     * Check rating is valid (1-5)
     */
    public boolean isValidRating(Integer rating) {
        return rating != null && rating >= 1 && rating <= 5;
    }
}