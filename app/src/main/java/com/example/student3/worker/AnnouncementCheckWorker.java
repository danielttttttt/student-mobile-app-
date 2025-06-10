package com.example.student3.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.student3.database.AppDatabase;
import com.example.student3.model.Announcement;
import com.example.student3.utils.NotificationHelper;

import java.util.List;

/**
 * WorkManager worker to periodically check for new announcements
 * This provides a more reliable way to check for announcements in the background
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Background Announcement Checker
 * @since 2024
 */
public class AnnouncementCheckWorker extends Worker {
    private static final String TAG = "AnnouncementCheckWorker";
    private static final String PREF_NAME = "AnnouncementWorkerPrefs";
    private static final String KEY_LAST_CHECK_TIME = "last_check_time";
    private static final String KEY_LAST_ANNOUNCEMENT_COUNT = "last_announcement_count";
    
    public AnnouncementCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    
    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d(TAG, "Starting announcement check work");
            
            Context context = getApplicationContext();
            AppDatabase database = AppDatabase.getDatabase(context);
            NotificationHelper notificationHelper = new NotificationHelper(context);
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            
            // Get current announcement count
            List<Announcement> allAnnouncements = database.announcementDao().getAllAnnouncementsSync();
            int currentCount = allAnnouncements != null ? allAnnouncements.size() : 0;
            
            // Get last known count
            int lastKnownCount = prefs.getInt(KEY_LAST_ANNOUNCEMENT_COUNT, 0);
            long lastCheckTime = prefs.getLong(KEY_LAST_CHECK_TIME, 0);
            
            Log.d(TAG, "Current announcements: " + currentCount + ", Last known: " + lastKnownCount);
            
            // Check if there are new announcements
            if (currentCount > lastKnownCount) {
                Log.d(TAG, "Found " + (currentCount - lastKnownCount) + " new announcements");
                
                // Get unread announcements to notify about
                List<Announcement> unreadAnnouncements = database.announcementDao().getUnreadAnnouncementsSync();
                
                if (unreadAnnouncements != null && !unreadAnnouncements.isEmpty()) {
                    // Show notification for the most recent unread announcement
                    Announcement latestAnnouncement = unreadAnnouncements.get(0);
                    
                    // Only notify if it's important or if it's the first check
                    if (latestAnnouncement.isImportant() || lastCheckTime == 0) {
                        notificationHelper.showAnnouncementNotification(latestAnnouncement);
                        Log.d(TAG, "Sent notification for: " + latestAnnouncement.getTitle());
                    }
                    
                    // If there are multiple unread announcements, show a summary notification
                    if (unreadAnnouncements.size() > 1) {
                        String summaryTitle = "New Announcements";
                        String summaryMessage = "You have " + unreadAnnouncements.size() + " unread announcements";
                        notificationHelper.showGeneralNotification(summaryTitle, summaryMessage);
                    }
                }
            }
            
            // Update stored values
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_LAST_ANNOUNCEMENT_COUNT, currentCount);
            editor.putLong(KEY_LAST_CHECK_TIME, System.currentTimeMillis());
            editor.apply();
            
            Log.d(TAG, "Announcement check work completed successfully");
            return Result.success();
            
        } catch (Exception e) {
            Log.e(TAG, "Error during announcement check work", e);
            return Result.retry();
        }
    }
}
