package com.example.student3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.student3.model.Student;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Secure session management with enhanced security features
 * 
 * SECURITY FEATURES:
 * - Session timeout management
 * - Secure session token generation
 * - Login tracking and monitoring
 * - Automatic session cleanup
 * - Session validation
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Secure Session Management
 * @since 2024
 */
public class SecureSessionManager {
    private static final String TAG = "SecureSessionManager";
    private static final String PREF_NAME = "SecureUserSession";
    
    // Session configuration
    private static final long SESSION_TIMEOUT_MS = 24 * 60 * 60 * 1000; // 24 hours
    private static final long IDLE_TIMEOUT_MS = 2 * 60 * 60 * 1000; // 2 hours
    
    // Session keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_DEPARTMENT = "userDepartment";
    private static final String KEY_SESSION_TOKEN = "sessionToken";
    private static final String KEY_LOGIN_TIME = "loginTime";
    private static final String KEY_LAST_ACTIVITY = "lastActivity";
    private static final String KEY_LOGIN_COUNT = "loginCount";
    
    private final SharedPreferences preferences;
    private final SimpleDateFormat dateFormat;

    public SecureSessionManager(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    /**
     * Create a secure login session for the user
     * 
     * @param student The authenticated student
     */
    public void createLoginSession(Student student) {
        if (student == null) {
            Log.e(TAG, "Cannot create session for null student");
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        String sessionToken = generateSecureToken();
        
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, student.getStudentId());
        editor.putString(KEY_USER_EMAIL, student.getEmail());
        editor.putString(KEY_USER_NAME, student.getFullName());
        editor.putInt(KEY_USER_DEPARTMENT, student.getDepartmentId());
        editor.putString(KEY_SESSION_TOKEN, sessionToken);
        editor.putLong(KEY_LOGIN_TIME, currentTime);
        editor.putLong(KEY_LAST_ACTIVITY, currentTime);
        
        // Increment login count
        int loginCount = preferences.getInt(KEY_LOGIN_COUNT, 0) + 1;
        editor.putInt(KEY_LOGIN_COUNT, loginCount);
        
        editor.apply();
        
        Log.i(TAG, "Secure session created for user: " + student.getEmail() + 
              " (Login #" + loginCount + ")");
    }

    /**
     * Check if user is currently logged in with valid session
     * 
     * @return true if user has valid session, false otherwise
     */
    public boolean isLoggedIn() {
        if (!preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return false;
        }
        
        // Check session timeout
        if (isSessionExpired()) {
            Log.w(TAG, "Session expired, logging out user");
            logoutUser();
            return false;
        }
        
        // Check idle timeout
        if (isIdleTimeoutReached()) {
            Log.w(TAG, "Idle timeout reached, logging out user");
            logoutUser();
            return false;
        }
        
        // Update last activity
        updateLastActivity();
        return true;
    }

    /**
     * Update last activity timestamp
     */
    public void updateLastActivity() {
        if (preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            preferences.edit()
                .putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
                .apply();
        }
    }

    /**
     * Get current user ID
     * 
     * @return User ID or -1 if not logged in
     */
    public int getUserId() {
        if (!isLoggedIn()) {
            return -1;
        }
        return preferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get current user email
     * 
     * @return User email or null if not logged in
     */
    public String getUserEmail() {
        if (!isLoggedIn()) {
            return null;
        }
        return preferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get current user name
     * 
     * @return User name or null if not logged in
     */
    public String getUserName() {
        if (!isLoggedIn()) {
            return null;
        }
        return preferences.getString(KEY_USER_NAME, null);
    }

    /**
     * Get current user department ID
     * 
     * @return Department ID or -1 if not logged in
     */
    public int getUserDepartment() {
        if (!isLoggedIn()) {
            return -1;
        }
        return preferences.getInt(KEY_USER_DEPARTMENT, -1);
    }

    /**
     * Get session information
     * 
     * @return SessionInfo object with session details
     */
    public SessionInfo getSessionInfo() {
        if (!isLoggedIn()) {
            return null;
        }
        
        SessionInfo info = new SessionInfo();
        info.userId = getUserId();
        info.userEmail = getUserEmail();
        info.userName = getUserName();
        info.userDepartment = getUserDepartment();
        info.loginTime = preferences.getLong(KEY_LOGIN_TIME, 0);
        info.lastActivity = preferences.getLong(KEY_LAST_ACTIVITY, 0);
        info.loginCount = preferences.getInt(KEY_LOGIN_COUNT, 0);
        info.sessionToken = preferences.getString(KEY_SESSION_TOKEN, null);
        
        return info;
    }

    /**
     * Logout user and clear session
     */
    public void logoutUser() {
        String userEmail = preferences.getString(KEY_USER_EMAIL, "unknown");
        
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_DEPARTMENT);
        editor.remove(KEY_SESSION_TOKEN);
        editor.remove(KEY_LOGIN_TIME);
        editor.remove(KEY_LAST_ACTIVITY);
        // Keep login count for analytics
        editor.apply();
        
        Log.i(TAG, "User logged out: " + userEmail);
    }

    /**
     * Check if session has expired
     * 
     * @return true if session expired, false otherwise
     */
    private boolean isSessionExpired() {
        long loginTime = preferences.getLong(KEY_LOGIN_TIME, 0);
        if (loginTime == 0) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - loginTime) > SESSION_TIMEOUT_MS;
    }

    /**
     * Check if idle timeout has been reached
     * 
     * @return true if idle timeout reached, false otherwise
     */
    private boolean isIdleTimeoutReached() {
        long lastActivity = preferences.getLong(KEY_LAST_ACTIVITY, 0);
        if (lastActivity == 0) {
            return true;
        }
        
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastActivity) > IDLE_TIMEOUT_MS;
    }

    /**
     * Generate a secure session token
     * 
     * @return Secure session token
     */
    private String generateSecureToken() {
        // Simple token generation - in production, use more sophisticated method
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000000);
        return "ST_" + timestamp + "_" + random;
    }

    /**
     * Get remaining session time in milliseconds
     * 
     * @return Remaining session time or 0 if expired
     */
    public long getRemainingSessionTime() {
        if (!preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return 0;
        }
        
        long loginTime = preferences.getLong(KEY_LOGIN_TIME, 0);
        if (loginTime == 0) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - loginTime;
        long remaining = SESSION_TIMEOUT_MS - elapsed;
        
        return Math.max(0, remaining);
    }

    /**
     * Get remaining idle time in milliseconds
     * 
     * @return Remaining idle time or 0 if expired
     */
    public long getRemainingIdleTime() {
        if (!preferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return 0;
        }
        
        long lastActivity = preferences.getLong(KEY_LAST_ACTIVITY, 0);
        if (lastActivity == 0) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastActivity;
        long remaining = IDLE_TIMEOUT_MS - elapsed;
        
        return Math.max(0, remaining);
    }

    /**
     * Session information container class
     */
    public static class SessionInfo {
        public int userId;
        public String userEmail;
        public String userName;
        public int userDepartment;
        public long loginTime;
        public long lastActivity;
        public int loginCount;
        public String sessionToken;
        
        public String getFormattedLoginTime() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return format.format(new Date(loginTime));
        }
        
        public String getFormattedLastActivity() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return format.format(new Date(lastActivity));
        }
    }
}
