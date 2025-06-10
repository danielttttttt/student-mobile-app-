package com.example.student3.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.student3.service.AnnouncementNotificationService;
import com.example.student3.worker.AnnouncementCheckWorker;

import java.util.concurrent.TimeUnit;

/**
 * Manages background notification checking for announcements
 * Handles both WorkManager periodic tasks and background service
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Background Notification Manager
 * @since 2024
 */
public class BackgroundNotificationManager {
    private static final String TAG = "BackgroundNotificationManager";
    private static final String WORK_NAME = "announcement_check_work";
    
    private final Context context;
    
    public BackgroundNotificationManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * Start background notification checking
     * Uses WorkManager for reliable background execution
     */
    public void startBackgroundChecking() {
        Log.d(TAG, "Starting background announcement checking");
        
        // Create constraints for the work
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // Works offline
            .setRequiresBatteryNotLow(true) // Don't run when battery is low
            .build();
        
        // Create periodic work request (minimum interval is 15 minutes)
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
            AnnouncementCheckWorker.class,
            15, TimeUnit.MINUTES) // Check every 15 minutes
            .setConstraints(constraints)
            .addTag("announcement_notifications")
            .build();
        
        // Enqueue the work (replace existing if any)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        );
        
        Log.d(TAG, "Background work scheduled successfully");
    }
    
    /**
     * Stop background notification checking
     */
    public void stopBackgroundChecking() {
        Log.d(TAG, "Stopping background announcement checking");
        
        // Cancel the periodic work
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
        
        // Stop the background service if running
        Intent serviceIntent = new Intent(context, AnnouncementNotificationService.class);
        context.stopService(serviceIntent);
        
        Log.d(TAG, "Background checking stopped");
    }
    
    /**
     * Start the background service (alternative to WorkManager)
     * Use this for more frequent checks when app is in foreground
     */
    public void startBackgroundService() {
        Log.d(TAG, "Starting background service");
        Intent serviceIntent = new Intent(context, AnnouncementNotificationService.class);
        context.startService(serviceIntent);
    }
    
    /**
     * Stop the background service
     */
    public void stopBackgroundService() {
        Log.d(TAG, "Stopping background service");
        Intent serviceIntent = new Intent(context, AnnouncementNotificationService.class);
        context.stopService(serviceIntent);
    }
    
    /**
     * Check if background checking is enabled based on user preferences
     */
    public boolean isBackgroundCheckingEnabled() {
        // Check user preferences for notifications
        // For now, return true - you can add SharedPreferences check here
        return true;
    }
    
    /**
     * Enable or disable background checking based on user preference
     */
    public void setBackgroundCheckingEnabled(boolean enabled) {
        if (enabled) {
            startBackgroundChecking();
        } else {
            stopBackgroundChecking();
        }
    }
    
    /**
     * Trigger an immediate check for new announcements
     */
    public void triggerImmediateCheck() {
        Log.d(TAG, "Triggering immediate announcement check");
        
        // Create one-time work request for immediate execution
        androidx.work.OneTimeWorkRequest immediateWork = 
            new androidx.work.OneTimeWorkRequest.Builder(AnnouncementCheckWorker.class)
                .addTag("immediate_check")
                .build();
        
        WorkManager.getInstance(context).enqueue(immediateWork);
    }
}
