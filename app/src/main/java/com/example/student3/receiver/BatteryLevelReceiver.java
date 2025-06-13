package com.example.student3.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.student3.R;

/**
 * BroadcastReceiver to monitor battery level changes
 * 
 * This receiver demonstrates system event handling by:
 * - Monitoring battery low/okay events
 * - Showing notifications to inform users about battery status
 * - Providing battery-aware functionality for the student app
 * 
 * Educational Purpose: Shows how Android apps can respond to system events
 * and provide better user experience by being battery-conscious.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {
    
    private static final String TAG = "BatteryLevelReceiver";
    private static final String CHANNEL_ID = "battery_notifications";
    private static final int NOTIFICATION_ID = 1001;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Battery event received: " + action);
        
        if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            handleBatteryLow(context);
        } else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            handleBatteryOkay(context);
        } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            handleBatteryChanged(context, intent);
        }
    }
    
    /**
     * Handle battery low event
     * Shows notification and toast to inform user
     */
    private void handleBatteryLow(Context context) {
        Log.i(TAG, "Battery is low - showing notification");
        
        // Show toast for immediate feedback
        Toast.makeText(context, "Battery Low: Consider saving your work", Toast.LENGTH_LONG).show();
        
        // Show notification
        showBatteryNotification(context, 
            "Battery Low", 
            "Your battery is running low. Consider saving your work and reducing app usage.",
            true);
    }
    
    /**
     * Handle battery okay event
     * Shows notification that battery level is back to normal
     */
    private void handleBatteryOkay(Context context) {
        Log.i(TAG, "Battery is okay - showing notification");
        
        // Show toast for immediate feedback
        Toast.makeText(context, "Battery Level Normal", Toast.LENGTH_SHORT).show();
        
        // Show notification
        showBatteryNotification(context,
            "Battery Normal",
            "Battery level is back to normal. You can resume normal app usage.",
            false);
    }
    
    /**
     * Handle battery changed event (for detailed battery info)
     * This provides more detailed battery information
     */
    private void handleBatteryChanged(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        
        if (level != -1 && scale != -1) {
            float batteryPct = (level / (float) scale) * 100;
            Log.d(TAG, "Battery level: " + batteryPct + "%");
            
            // Show critical battery warning at 10%
            if (batteryPct <= 10 && batteryPct > 5) {
                showBatteryNotification(context,
                    "Critical Battery Level",
                    String.format("Battery at %.0f%%. Please charge your device soon.", batteryPct),
                    true);
            }
        }
    }
    
    /**
     * Show battery-related notification
     */
    private void showBatteryNotification(Context context, String title, String message, boolean isUrgent) {
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Create notification channel for Android 8.0+
        createNotificationChannel(notificationManager);
        
        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(isUrgent ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);
        
        // Show notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    
    /**
     * Create notification channel for Android 8.0+
     */
    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Battery Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications about battery level changes");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
