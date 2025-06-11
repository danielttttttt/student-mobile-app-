package com.example.student3.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling course scheduling and time periods.
 * 
 * The school operates with 8 periods from 2:30 PM to 11:30 PM (9 hours total).
 * Each period is approximately 1 hour and 7.5 minutes (67.5 minutes).
 * 
 * Period Schedule:
 * Period 1: 2:30 PM - 3:37 PM
 * Period 2: 3:37 PM - 4:45 PM
 * Period 3: 4:45 PM - 5:52 PM
 * Period 4: 5:52 PM - 7:00 PM
 * Period 5: 7:00 PM - 8:07 PM
 * Period 6: 8:07 PM - 9:15 PM
 * Period 7: 9:15 PM - 10:22 PM
 * Period 8: 10:22 PM - 11:30 PM
 */
public class ScheduleUtils {
    
    public static final int TOTAL_PERIODS = 8;
    public static final String START_TIME = "14:30"; // 2:30 PM in 24-hour format
    public static final String END_TIME = "23:30";   // 11:30 PM in 24-hour format
    public static final int MINUTES_PER_PERIOD = 67; // 67.5 minutes rounded down
    
    // Days of the week
    public static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    
    /**
     * Get the time range for a specific period
     * @param period Period number (1-8)
     * @return String representation of time range (e.g., "2:30 PM - 3:37 PM")
     */
    public static String getPeriodTimeRange(int period) {
        if (period < 1 || period > TOTAL_PERIODS) {
            return "Invalid Period";
        }
        
        // Calculate start time for the period
        int startMinutes = 150 + (period - 1) * MINUTES_PER_PERIOD; // 150 = 2:30 PM in minutes from midnight
        int endMinutes = startMinutes + MINUTES_PER_PERIOD;
        
        String startTime = formatTime(startMinutes);
        String endTime = formatTime(endMinutes);
        
        return startTime + " - " + endTime;
    }
    
    /**
     * Get time range for multiple periods
     * @param startPeriod Starting period (1-8)
     * @param endPeriod Ending period (1-8)
     * @return String representation of time range
     */
    public static String getPeriodTimeRange(int startPeriod, int endPeriod) {
        if (startPeriod < 1 || startPeriod > TOTAL_PERIODS || 
            endPeriod < 1 || endPeriod > TOTAL_PERIODS || 
            startPeriod > endPeriod) {
            return "Invalid Period Range";
        }
        
        int startMinutes = 150 + (startPeriod - 1) * MINUTES_PER_PERIOD;
        int endMinutes = 150 + endPeriod * MINUTES_PER_PERIOD;
        
        String startTime = formatTime(startMinutes);
        String endTime = formatTime(endMinutes);
        
        return startTime + " - " + endTime;
    }
    
    /**
     * Format minutes from midnight to 12-hour time format
     * @param minutes Minutes from midnight
     * @return Formatted time string (e.g., "2:30 PM")
     */
    private static String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        
        String period = hours >= 12 ? "PM" : "AM";
        int displayHour = hours > 12 ? hours - 12 : (hours == 0 ? 12 : hours);
        
        return String.format("%d:%02d %s", displayHour, mins, period);
    }
    
    /**
     * Calculate how many periods are needed for a given number of weekly hours
     * @param weeklyHours Total weekly hours for the course
     * @param daysPerWeek Number of days the course meets per week
     * @return Number of periods needed per day
     */
    public static int calculatePeriodsNeeded(int weeklyHours, int daysPerWeek) {
        if (daysPerWeek <= 0) return 1;
        
        double hoursPerDay = (double) weeklyHours / daysPerWeek;
        // Each period is approximately 1.125 hours (67.5 minutes)
        double periodsNeeded = hoursPerDay / 1.125;
        
        return Math.max(1, (int) Math.ceil(periodsNeeded));
    }
    
    /**
     * Generate a schedule string for display
     * @param daysOfWeek Comma-separated days (e.g., "MON,WED,FRI")
     * @param startPeriod Starting period
     * @param endPeriod Ending period
     * @return Formatted schedule string
     */
    public static String formatSchedule(String daysOfWeek, int startPeriod, int endPeriod) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return "Schedule TBD";
        }
        
        String[] days = daysOfWeek.split(",");
        StringBuilder schedule = new StringBuilder();
        
        // Format days
        for (int i = 0; i < days.length; i++) {
            if (i > 0) schedule.append(", ");
            schedule.append(formatDayName(days[i].trim()));
        }
        
        // Add time range
        schedule.append(" ");
        schedule.append(getPeriodTimeRange(startPeriod, endPeriod));
        
        return schedule.toString();
    }
    
    /**
     * Format day abbreviation to full name
     * @param dayAbbr Day abbreviation (MON, TUE, etc.)
     * @return Full day name
     */
    private static String formatDayName(String dayAbbr) {
        switch (dayAbbr.toUpperCase()) {
            case "MON": return "Monday";
            case "TUE": return "Tuesday";
            case "WED": return "Wednesday";
            case "THU": return "Thursday";
            case "FRI": return "Friday";
            case "SAT": return "Saturday";
            case "SUN": return "Sunday";
            default: return dayAbbr;
        }
    }

    /**
     * Format period start time for display
     * @param period Period number (1-8)
     * @return Formatted start time (e.g., "2:30 PM")
     */
    public static String formatPeriodTime(int period) {
        if (period < 1 || period > TOTAL_PERIODS) {
            return "TBD";
        }

        int startMinutes = 150 + (period - 1) * MINUTES_PER_PERIOD;
        return formatTime(startMinutes);
    }
    
    /**
     * Get common scheduling patterns for courses
     * @param creditHours Number of credit hours
     * @return List of suggested scheduling patterns
     */
    public static List<SchedulePattern> getSuggestedSchedules(int creditHours) {
        List<SchedulePattern> patterns = new ArrayList<>();
        
        switch (creditHours) {
            case 3:
                patterns.add(new SchedulePattern("MON,WED,FRI", 1, 1, 3));
                patterns.add(new SchedulePattern("TUE,THU", 1, 2, 3));
                break;
            case 4:
                patterns.add(new SchedulePattern("MON,WED,FRI", 1, 1, 4));
                patterns.add(new SchedulePattern("TUE,THU", 1, 2, 4));
                patterns.add(new SchedulePattern("MON,WED", 1, 2, 4));
                break;
            case 5:
                patterns.add(new SchedulePattern("MON,WED,FRI", 1, 2, 5));
                patterns.add(new SchedulePattern("TUE,THU", 1, 3, 5));
                break;
            default:
                patterns.add(new SchedulePattern("MON,WED,FRI", 1, 1, creditHours));
                break;
        }
        
        return patterns;
    }
    
    /**
     * Class to represent a schedule pattern
     */
    public static class SchedulePattern {
        public final String daysOfWeek;
        public final int startPeriod;
        public final int endPeriod;
        public final int totalWeeklyHours;
        
        public SchedulePattern(String daysOfWeek, int startPeriod, int endPeriod, int totalWeeklyHours) {
            this.daysOfWeek = daysOfWeek;
            this.startPeriod = startPeriod;
            this.endPeriod = endPeriod;
            this.totalWeeklyHours = totalWeeklyHours;
        }
        
        @Override
        public String toString() {
            return formatSchedule(daysOfWeek, startPeriod, endPeriod);
        }
    }
}
