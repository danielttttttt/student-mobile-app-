package com.example.student3.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.example.student3.receiver.BatteryLevelReceiver;

/**
 * Utility class for battery monitoring functionality
 * 
 * This class provides helper methods to:
 * - Register/unregister battery receivers dynamically
 * - Get current battery information
 * - Determine if the app should reduce background operations
 * 
 * Educational Purpose: Shows how to manage system resources and
 * create battery-aware applications.
 */
public class BatteryMonitorUtil {
    
    private static final String TAG = "BatteryMonitorUtil";
    private static BatteryLevelReceiver batteryReceiver;
    
    /**
     * Register battery level receiver dynamically
     * Call this when the app becomes active and needs detailed battery monitoring
     */
    public static void registerBatteryReceiver(Context context) {
        if (batteryReceiver == null) {
            batteryReceiver = new BatteryLevelReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            
            try {
                context.registerReceiver(batteryReceiver, filter);
                Log.d(TAG, "Battery receiver registered for detailed monitoring");
            } catch (Exception e) {
                Log.e(TAG, "Failed to register battery receiver", e);
            }
        }
    }
    
    /**
     * Unregister battery level receiver
     * Call this when the app goes to background to save resources
     */
    public static void unregisterBatteryReceiver(Context context) {
        if (batteryReceiver != null) {
            try {
                context.unregisterReceiver(batteryReceiver);
                batteryReceiver = null;
                Log.d(TAG, "Battery receiver unregistered");
            } catch (Exception e) {
                Log.e(TAG, "Failed to unregister battery receiver", e);
            }
        }
    }
    
    /**
     * Get current battery level percentage
     * @return Battery level as percentage (0-100), or -1 if unavailable
     */
    public static int getCurrentBatteryLevel(Context context) {
        try {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to get battery level", e);
        }
        return -1;
    }
    
    /**
     * Check if battery is currently low (below 20%)
     * @return true if battery is low, false otherwise
     */
    public static boolean isBatteryLow(Context context) {
        int batteryLevel = getCurrentBatteryLevel(context);
        return batteryLevel != -1 && batteryLevel < 20;
    }
    
    /**
     * Check if battery is critically low (below 10%)
     * @return true if battery is critically low, false otherwise
     */
    public static boolean isBatteryCritical(Context context) {
        int batteryLevel = getCurrentBatteryLevel(context);
        return batteryLevel != -1 && batteryLevel < 10;
    }
    
    /**
     * Check if the device is currently charging
     * @return true if charging, false otherwise
     */
    public static boolean isCharging(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            
            if (batteryStatus != null) {
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                       status == BatteryManager.BATTERY_STATUS_FULL;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to check charging status", e);
        }
        return false;
    }
    
    /**
     * Determine if app should reduce background operations based on battery level
     * @return true if app should conserve battery, false otherwise
     */
    public static boolean shouldConserveBattery(Context context) {
        return isBatteryLow(context) && !isCharging(context);
    }
    
    /**
     * Get battery status description for UI display
     * @return Human-readable battery status string
     */
    public static String getBatteryStatusDescription(Context context) {
        int level = getCurrentBatteryLevel(context);
        boolean charging = isCharging(context);
        
        if (level == -1) {
            return "Battery status unavailable";
        }
        
        String status = level + "%";
        if (charging) {
            status += " (Charging)";
        } else if (level < 10) {
            status += " (Critical - Please charge)";
        } else if (level < 20) {
            status += " (Low - Consider charging)";
        } else {
            status += " (Normal)";
        }
        
        return status;
    }
}
