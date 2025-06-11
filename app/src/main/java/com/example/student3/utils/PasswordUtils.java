package com.example.student3.utils;

import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Utility class for secure password handling
 * Provides password hashing, verification, and strength validation
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Secure Authentication Implementation
 * @since 2025
 */
public class PasswordUtils {
    private static final String TAG = "PasswordUtils";
    
    // Password strength requirements
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;
    
    // Regex patterns for password validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    
    // Salt length for password hashing
    private static final int SALT_LENGTH = 32;

    /**
     * Hash a password with salt using SHA-256
     * 
     * @param password The plain text password
     * @return The hashed password with salt (format: salt:hash)
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Encode salt and hash to Base64
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashString = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Return format: salt:hash
            return saltString + ":" + hashString;
            
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            return null;
        }
    }

    /**
     * Verify a password against a stored hash
     * 
     * @param password The plain text password to verify
     * @param storedHash The stored hash (format: salt:hash)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                Log.e(TAG, "Invalid stored hash format");
                return false;
            }
            
            // Decode salt and hash
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String expectedHash = parts[1];
            
            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            String actualHash = Base64.getEncoder().encodeToString(hashedPassword);
            
            // Compare hashes
            return expectedHash.equals(actualHash);
            
        } catch (Exception e) {
            Log.e(TAG, "Error verifying password", e);
            return false;
        }
    }

    /**
     * Validate password strength
     * 
     * @param password The password to validate
     * @return PasswordValidationResult containing validation details
     */
    public static PasswordValidationResult validatePasswordStrength(String password) {
        PasswordValidationResult result = new PasswordValidationResult();
        
        if (password == null) {
            result.isValid = false;
            result.errorMessage = "Password cannot be null";
            return result;
        }
        
        // Check length
        if (password.length() < MIN_PASSWORD_LENGTH) {
            result.isValid = false;
            result.errorMessage = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long";
            return result;
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            result.isValid = false;
            result.errorMessage = "Password must be less than " + MAX_PASSWORD_LENGTH + " characters long";
            return result;
        }
        
        // Check for uppercase letter
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            result.isValid = false;
            result.errorMessage = "Password must contain at least one uppercase letter";
            return result;
        }
        
        // Check for lowercase letter
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            result.isValid = false;
            result.errorMessage = "Password must contain at least one lowercase letter";
            return result;
        }
        
        // Check for digit
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            result.isValid = false;
            result.errorMessage = "Password must contain at least one number";
            return result;
        }
        
        // Check for special character
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            result.isValid = false;
            result.errorMessage = "Password must contain at least one special character";
            return result;
        }
        
        // All checks passed
        result.isValid = true;
        result.errorMessage = null;
        return result;
    }

    /**
     * Generate a secure random password
     * 
     * @param length The desired password length
     * @return A randomly generated password
     */
    public static String generateSecurePassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            length = MIN_PASSWORD_LENGTH;
        }
        
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String allChars = upperCase + lowerCase + digits + specialChars;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password
        for (int i = 0; i < password.length(); i++) {
            int randomIndex = random.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }
        
        return password.toString();
    }

    /**
     * Result class for password validation
     */
    public static class PasswordValidationResult {
        public boolean isValid;
        public String errorMessage;
        
        public PasswordValidationResult() {
            this.isValid = false;
            this.errorMessage = null;
        }
    }
}
