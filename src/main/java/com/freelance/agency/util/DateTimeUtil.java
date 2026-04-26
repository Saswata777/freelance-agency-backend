package com.freelance.agency.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeUtil {

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private static final DateTimeFormatter LOG_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Format for display in emails and UI
     * Example: 28 Apr 2025, 10:30 AM
     */
    public String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Format for logs
     * Example: 2025-04-28 10:30:00
     */
    public String formatForLog(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(LOG_FORMATTER);
    }

    /**
     * Check if a datetime is in the past
     */
    public boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Check if a datetime is within next N minutes
     */
    public boolean isWithinMinutes(LocalDateTime dateTime, int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(minutes);
        return dateTime.isAfter(now) && dateTime.isBefore(threshold);
    }

    /**
     * Get end time based on start + duration
     */
    public LocalDateTime getEndTime(LocalDateTime startTime, int durationMinutes) {
        return startTime.plusMinutes(durationMinutes);
    }
}