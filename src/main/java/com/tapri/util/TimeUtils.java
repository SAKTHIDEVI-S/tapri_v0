package com.tapri.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        long weeks = ChronoUnit.WEEKS.between(dateTime, now);
        long months = ChronoUnit.MONTHS.between(dateTime, now);
        long years = ChronoUnit.YEARS.between(dateTime, now);
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (days < 7) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (weeks < 4) {
            return weeks + " week" + (weeks == 1 ? "" : "s") + " ago";
        } else if (months < 12) {
            return months + " month" + (months == 1 ? "" : "s") + " ago";
        } else {
            return years + " year" + (years == 1 ? "" : "s") + " ago";
        }
    }
}
