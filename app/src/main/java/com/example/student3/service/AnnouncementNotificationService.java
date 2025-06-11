package com.example.student3.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.student3.database.AppDatabase;
import com.example.student3.model.Announcement;
import com.example.student3.utils.NotificationHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Background service to check for new announcements and send notifications
 * This service runs periodically to check for new announcements even when the app is closed
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Background Notification Service
 * @since 2025
 */
public class AnnouncementNotificationService extends Service {
    private static final String TAG = "AnnouncementNotificationService";
    private static final long CHECK_INTERVAL = 30 * 60 * 1000; // Check every 30 minutes
    
    private Handler handler;
    private Runnable checkRunnable;
    private ExecutorService executor;
    private NotificationHelper notificationHelper;
    private AppDatabase database;
    private long lastCheckTime;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AnnouncementNotificationService created");
        
        handler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();
        notificationHelper = new NotificationHelper(this);
        database = AppDatabase.getDatabase(this);
        lastCheckTime = System.currentTimeMillis();
        
        setupPeriodicCheck();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AnnouncementNotificationService started");
        startPeriodicCheck();
        return START_STICKY; // Restart service if killed by system
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // This is not a bound service
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AnnouncementNotificationService destroyed");
        stopPeriodicCheck();
        if (executor != null) {
            executor.shutdown();
        }
    }
    
    private void setupPeriodicCheck() {
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewAnnouncements();
                // Schedule next check
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
    }
    
    private void startPeriodicCheck() {
        if (handler != null && checkRunnable != null) {
            handler.post(checkRunnable);
        }
    }
    
    private void stopPeriodicCheck() {
        if (handler != null && checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
        }
    }
    
    private void checkForNewAnnouncements() {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Checking for new announcements...");
                
                // Get all announcements (in a real app, you'd query by timestamp)
                List<Announcement> allAnnouncements = database.announcementDao().getAllAnnouncementsSync();
                
                if (allAnnouncements != null) {
                    for (Announcement announcement : allAnnouncements) {
                        // Check if this is a new announcement (created after last check)
                        // In a real implementation, you'd compare timestamps
                        if (isNewAnnouncement(announcement)) {
                            Log.d(TAG, "Found new announcement: " + announcement.getTitle());
                            notificationHelper.showAnnouncementNotification(announcement);
                        }
                    }
                }
                
                lastCheckTime = System.currentTimeMillis();
                Log.d(TAG, "Finished checking for new announcements");
                
            } catch (Exception e) {
                Log.e(TAG, "Error checking for new announcements", e);
            }
        });
    }
    
    private boolean isNewAnnouncement(Announcement announcement) {
        // Simple check - in a real app you'd use proper timestamp comparison
        // For now, we'll check if the announcement is unread and important
        return !announcement.isRead() && announcement.isImportant();
    }
}
