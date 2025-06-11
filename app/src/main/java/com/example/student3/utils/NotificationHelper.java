package com.example.student3.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.student3.R;
import com.example.student3.model.Announcement;
import com.example.student3.ui.MainActivity;

/**
 * Utility class for managing notifications in the student app
 * 
 * Features:
 * - Creates notification channels for different types of notifications
 * - Handles announcement notifications
 * - Manages notification permissions and settings
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Notification System
 * @since 2025
 */
public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    
    // Notification channels
    public static final String CHANNEL_ANNOUNCEMENTS = "announcements";
    public static final String CHANNEL_REMINDERS = "reminders";
    public static final String CHANNEL_GENERAL = "general";
    
    // Notification IDs
    private static final int NOTIFICATION_ID_ANNOUNCEMENT = 1001;
    private static final int NOTIFICATION_ID_REMINDER = 1002;
    
    private final Context context;
    private final NotificationManagerCompat notificationManager;
    
    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = NotificationManagerCompat.from(this.context);
        createNotificationChannels();
    }
    
    /**
     * Create notification channels for Android 8.0+ (API level 26+)
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Announcements channel
            NotificationChannel announcementsChannel = new NotificationChannel(
                CHANNEL_ANNOUNCEMENTS,
                "Announcements",
                NotificationManager.IMPORTANCE_HIGH
            );
            announcementsChannel.setDescription("Important school announcements and updates");
            announcementsChannel.enableVibration(true);
            announcementsChannel.setShowBadge(true);
            
            // Reminders channel
            NotificationChannel remindersChannel = new NotificationChannel(
                CHANNEL_REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            remindersChannel.setDescription("Course and schedule reminders");
            remindersChannel.enableVibration(false);
            remindersChannel.setShowBadge(true);
            
            // General channel
            NotificationChannel generalChannel = new NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_LOW
            );
            generalChannel.setDescription("General app notifications");
            generalChannel.enableVibration(false);
            generalChannel.setShowBadge(false);
            
            // Register channels with the system
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(announcementsChannel);
                manager.createNotificationChannel(remindersChannel);
                manager.createNotificationChannel(generalChannel);
            }
        }
    }
    
    /**
     * Show notification for a new announcement
     * 
     * @param announcement The announcement to notify about
     */
    public void showAnnouncementNotification(Announcement announcement) {
        if (!areNotificationsEnabled()) {
            return;
        }
        
        // Create intent to open the app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Determine notification priority based on announcement importance
        int priority = announcement.isImportant() ? 
            NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT;
        
        String channelId = CHANNEL_ANNOUNCEMENTS;
        
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_announcement) // You'll need to create this icon
            .setContentTitle(announcement.isImportant() ? "Important: " + announcement.getTitle() : announcement.getTitle())
            .setContentText(announcement.getContent())
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(announcement.getContent()))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        
        // Add vibration for important announcements
        if (announcement.isImportant()) {
            builder.setVibrate(new long[]{0, 250, 250, 250});
        }
        
        // Show the notification
        try {
            notificationManager.notify(NOTIFICATION_ID_ANNOUNCEMENT + announcement.getAnnouncementId(), builder.build());
        } catch (SecurityException e) {
            // Handle case where notification permission is not granted
            android.util.Log.w(TAG, "Notification permission not granted", e);
        }
    }
    
    /**
     * Check if notifications are enabled for the app
     * 
     * @return true if notifications are enabled, false otherwise
     */
    public boolean areNotificationsEnabled() {
        return notificationManager.areNotificationsEnabled();
    }
    
    /**
     * Cancel a specific announcement notification
     * 
     * @param announcementId The ID of the announcement notification to cancel
     */
    public void cancelAnnouncementNotification(int announcementId) {
        notificationManager.cancel(NOTIFICATION_ID_ANNOUNCEMENT + announcementId);
    }
    
    /**
     * Cancel all notifications
     */
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
    
    /**
     * Show a general notification
     * 
     * @param title The notification title
     * @param message The notification message
     */
    public void showGeneralNotification(String title, String message) {
        if (!areNotificationsEnabled()) {
            return;
        }
        
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to create this icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);
        
        try {
            notificationManager.notify(NOTIFICATION_ID_REMINDER, builder.build());
        } catch (SecurityException e) {
            android.util.Log.w(TAG, "Notification permission not granted", e);
        }
    }
}
