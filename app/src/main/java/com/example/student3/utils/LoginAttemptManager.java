package com.example.student3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Manages login attempts and account lockout functionality
 * Provides security against brute force attacks
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Secure Authentication Implementation
 * @since 2025
 */
public class LoginAttemptManager {
    private static final String TAG = "LoginAttemptManager";
    private static final String PREF_NAME = "LoginAttempts";
    
    // Security configuration
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    private static final long ATTEMPT_RESET_DURATION_MS = 60 * 60 * 1000; // 1 hour
    
    // SharedPreferences keys
    private static final String KEY_ATTEMPT_COUNT = "_attempt_count";
    private static final String KEY_LAST_ATTEMPT = "_last_attempt";
    private static final String KEY_LOCKOUT_TIME = "_lockout_time";
    
    private final SharedPreferences preferences;
    private final SimpleDateFormat dateFormat;

    public LoginAttemptManager(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    /**
     * Record a failed login attempt for an email
     * 
     * @param email The email address that failed login
     * @return true if account should be locked, false otherwise
     */
    public boolean recordFailedAttempt(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailKey = email.toLowerCase().trim();
        long currentTime = System.currentTimeMillis();
        
        // Get current attempt count
        int currentAttempts = preferences.getInt(emailKey + KEY_ATTEMPT_COUNT, 0);
        long lastAttemptTime = preferences.getLong(emailKey + KEY_LAST_ATTEMPT, 0);
        
        // Reset attempts if enough time has passed
        if (currentTime - lastAttemptTime > ATTEMPT_RESET_DURATION_MS) {
            currentAttempts = 0;
        }
        
        // Increment attempt count
        currentAttempts++;
        
        // Save updated attempt data
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(emailKey + KEY_ATTEMPT_COUNT, currentAttempts);
        editor.putLong(emailKey + KEY_LAST_ATTEMPT, currentTime);
        
        // Check if account should be locked
        if (currentAttempts >= MAX_LOGIN_ATTEMPTS) {
            editor.putLong(emailKey + KEY_LOCKOUT_TIME, currentTime);
            Log.w(TAG, "Account locked for email: " + emailKey + " after " + currentAttempts + " failed attempts");
        }
        
        editor.apply();
        
        return currentAttempts >= MAX_LOGIN_ATTEMPTS;
    }

    /**
     * Record a successful login attempt for an email
     * Clears any existing failed attempts
     * 
     * @param email The email address that successfully logged in
     */
    public void recordSuccessfulLogin(String email) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        
        String emailKey = email.toLowerCase().trim();
        
        // Clear all attempt data for this email
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(emailKey + KEY_ATTEMPT_COUNT);
        editor.remove(emailKey + KEY_LAST_ATTEMPT);
        editor.remove(emailKey + KEY_LOCKOUT_TIME);
        editor.apply();
        
        Log.d(TAG, "Cleared login attempts for successful login: " + emailKey);
    }

    /**
     * Check if an account is currently locked
     * 
     * @param email The email address to check
     * @return true if account is locked, false otherwise
     */
    public boolean isAccountLocked(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailKey = email.toLowerCase().trim();
        long lockoutTime = preferences.getLong(emailKey + KEY_LOCKOUT_TIME, 0);
        
        if (lockoutTime == 0) {
            return false; // Never been locked
        }
        
        long currentTime = System.currentTimeMillis();
        boolean isLocked = (currentTime - lockoutTime) < LOCKOUT_DURATION_MS;
        
        // If lockout period has expired, clear the lockout
        if (!isLocked) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(emailKey + KEY_ATTEMPT_COUNT);
            editor.remove(emailKey + KEY_LAST_ATTEMPT);
            editor.remove(emailKey + KEY_LOCKOUT_TIME);
            editor.apply();
            Log.d(TAG, "Lockout expired for email: " + emailKey);
        }
        
        return isLocked;
    }

    /**
     * Get the number of failed login attempts for an email
     * 
     * @param email The email address to check
     * @return The number of failed attempts
     */
    public int getFailedAttemptCount(String email) {
        if (email == null || email.trim().isEmpty()) {
            return 0;
        }
        
        String emailKey = email.toLowerCase().trim();
        long currentTime = System.currentTimeMillis();
        long lastAttemptTime = preferences.getLong(emailKey + KEY_LAST_ATTEMPT, 0);
        
        // Reset attempts if enough time has passed
        if (currentTime - lastAttemptTime > ATTEMPT_RESET_DURATION_MS) {
            return 0;
        }
        
        return preferences.getInt(emailKey + KEY_ATTEMPT_COUNT, 0);
    }

    /**
     * Get the remaining lockout time in milliseconds
     * 
     * @param email The email address to check
     * @return Remaining lockout time in milliseconds, 0 if not locked
     */
    public long getRemainingLockoutTime(String email) {
        if (email == null || email.trim().isEmpty()) {
            return 0;
        }
        
        String emailKey = email.toLowerCase().trim();
        long lockoutTime = preferences.getLong(emailKey + KEY_LOCKOUT_TIME, 0);
        
        if (lockoutTime == 0) {
            return 0; // Never been locked
        }
        
        long currentTime = System.currentTimeMillis();
        long remainingTime = LOCKOUT_DURATION_MS - (currentTime - lockoutTime);
        
        return Math.max(0, remainingTime);
    }

    /**
     * Get a user-friendly lockout message
     * 
     * @param email The email address to check
     * @return Lockout message or null if not locked
     */
    public String getLockoutMessage(String email) {
        if (!isAccountLocked(email)) {
            return null;
        }
        
        long remainingTime = getRemainingLockoutTime(email);
        long remainingMinutes = remainingTime / (60 * 1000);
        
        if (remainingMinutes > 0) {
            return "Account locked. Try again in " + remainingMinutes + " minute(s).";
        } else {
            return "Account locked. Try again in less than a minute.";
        }
    }

    /**
     * Manually unlock an account (for admin purposes)
     * 
     * @param email The email address to unlock
     */
    public void unlockAccount(String email) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        
        String emailKey = email.toLowerCase().trim();
        
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(emailKey + KEY_ATTEMPT_COUNT);
        editor.remove(emailKey + KEY_LAST_ATTEMPT);
        editor.remove(emailKey + KEY_LOCKOUT_TIME);
        editor.apply();
        
        Log.i(TAG, "Manually unlocked account: " + emailKey);
    }

    /**
     * Get maximum allowed login attempts
     * 
     * @return Maximum login attempts before lockout
     */
    public static int getMaxLoginAttempts() {
        return MAX_LOGIN_ATTEMPTS;
    }

    /**
     * Get lockout duration in milliseconds
     * 
     * @return Lockout duration in milliseconds
     */
    public static long getLockoutDuration() {
        return LOCKOUT_DURATION_MS;
    }
}
