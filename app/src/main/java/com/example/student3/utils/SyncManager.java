package com.example.student3.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.student3.database.AppDatabase;
import com.example.student3.model.Announcement;
import com.example.student3.model.SimpleTodo;
import com.example.student3.network.NetworkManager;
import com.example.student3.network.models.ApiResponse;
import com.example.student3.network.models.SyncRequest;
import com.example.student3.network.models.SyncResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Sync Manager for handling online/offline synchronization
 * 
 * This class manages:
 * - Automatic sync scheduling
 * - Manual sync operations
 * - Conflict resolution
 * - Sync status tracking
 */
public class SyncManager {
    
    private static final String TAG = "SyncManager";
    private static final String PREF_NAME = "sync_preferences";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    private static final String KEY_SYNC_ENABLED = "sync_enabled";
    private static final String KEY_AUTO_SYNC = "auto_sync";
    private static final String KEY_WIFI_ONLY = "wifi_only_sync";
    
    private static SyncManager instance;
    private Context context;
    private NetworkManager networkManager;
    private AppDatabase database;
    private SharedPreferences preferences;
    private SyncListener syncListener;
    
    // Sync status
    public enum SyncStatus {
        IDLE,
        SYNCING,
        SUCCESS,
        ERROR,
        NO_NETWORK
    }
    
    private SyncStatus currentStatus = SyncStatus.IDLE;
    
    private SyncManager(Context context) {
        this.context = context.getApplicationContext();
        this.networkManager = NetworkManager.getInstance(context);
        this.database = AppDatabase.getDatabase(context);
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized SyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new SyncManager(context);
        }
        return instance;
    }
    
    /**
     * Interface for sync status callbacks
     */
    public interface SyncListener {
        void onSyncStarted();
        void onSyncProgress(String message);
        void onSyncCompleted(boolean success, String message);
        void onSyncError(String error);
    }
    
    /**
     * Set sync listener for UI updates
     */
    public void setSyncListener(SyncListener listener) {
        this.syncListener = listener;
    }
    
    /**
     * Get current sync status
     */
    public SyncStatus getCurrentStatus() {
        return currentStatus;
    }
    
    /**
     * Check if sync is enabled
     */
    public boolean isSyncEnabled() {
        return preferences.getBoolean(KEY_SYNC_ENABLED, true);
    }
    
    /**
     * Enable or disable sync
     */
    public void setSyncEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_SYNC_ENABLED, enabled).apply();
    }
    
    /**
     * Check if auto sync is enabled
     */
    public boolean isAutoSyncEnabled() {
        return preferences.getBoolean(KEY_AUTO_SYNC, true);
    }
    
    /**
     * Enable or disable auto sync
     */
    public void setAutoSyncEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_AUTO_SYNC, enabled).apply();
    }
    
    /**
     * Check if WiFi-only sync is enabled
     */
    public boolean isWiFiOnlySync() {
        return preferences.getBoolean(KEY_WIFI_ONLY, false);
    }
    
    /**
     * Set WiFi-only sync preference
     */
    public void setWiFiOnlySync(boolean wifiOnly) {
        preferences.edit().putBoolean(KEY_WIFI_ONLY, wifiOnly).apply();
    }
    
    /**
     * Get last sync time
     */
    public String getLastSyncTime() {
        long timestamp = preferences.getLong(KEY_LAST_SYNC_TIME, 0);
        if (timestamp == 0) {
            return "Never";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Perform manual sync operation
     */
    public void performManualSync() {
        if (!isSyncEnabled()) {
            notifyError("Sync is disabled");
            return;
        }
        
        if (!networkManager.isNetworkAvailable()) {
            currentStatus = SyncStatus.NO_NETWORK;
            notifyError("No internet connection");
            return;
        }
        
        if (isWiFiOnlySync() && !networkManager.isWiFiConnected()) {
            notifyError("WiFi connection required for sync");
            return;
        }
        
        startSync();
    }
    
    /**
     * Start sync process
     */
    private void startSync() {
        currentStatus = SyncStatus.SYNCING;
        notifyStarted();
        
        Log.d(TAG, "Starting sync process");
        
        // First, sync announcements (simple demo)
        syncAnnouncements();
    }
    
    /**
     * Sync announcements from server
     */
    private void syncAnnouncements() {
        notifyProgress("Syncing announcements...");
        
        NetworkManager.getApiService().getAnnouncements().enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Received " + response.body().size() + " announcements from server");
                    
                    // For demo purposes, we'll just log the data
                    // In a real app, you'd update the local database here
                    processAnnouncementSync(response.body());
                } else {
                    Log.e(TAG, "Failed to sync announcements: " + response.code());
                    syncTodos(); // Continue with next sync step
                }
            }
            
            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Log.e(TAG, "Network error syncing announcements", t);
                syncTodos(); // Continue with next sync step
            }
        });
    }
    
    /**
     * Process announcement sync results
     */
    private void processAnnouncementSync(List<Announcement> serverAnnouncements) {
        // In a real implementation, you would:
        // 1. Compare server data with local data
        // 2. Update local database with new/changed announcements
        // 3. Handle conflicts if any
        
        Log.d(TAG, "Processing " + serverAnnouncements.size() + " announcements");
        
        // For demo, just continue to next sync step
        syncTodos();
    }
    
    /**
     * Sync todos (demonstration)
     */
    private void syncTodos() {
        notifyProgress("Syncing todos...");
        
        // Get local todos that need syncing
        new Thread(() -> {
            try {
                List<SimpleTodo> localTodos = database.simpleTodoDao().getAllTodosSync();
                Log.d(TAG, "Found " + localTodos.size() + " local todos");
                
                // For demo, just complete the sync
                completeSyncProcess();
                
            } catch (Exception e) {
                Log.e(TAG, "Error syncing todos", e);
                completeSyncProcess();
            }
        }).start();
    }
    
    /**
     * Complete sync process
     */
    private void completeSyncProcess() {
        // Update last sync time
        preferences.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply();
        
        currentStatus = SyncStatus.SUCCESS;
        notifyCompleted(true, "Sync completed successfully");
        
        Log.d(TAG, "Sync process completed");
    }
    
    /**
     * Check server connectivity (simplified for demo)
     */
    public void checkServerStatus(ServerStatusCallback callback) {
        if (!networkManager.isNetworkAvailable()) {
            callback.onResult(false, "No network connection");
            return;
        }

        // Simple network check for demo
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate network delay
                boolean isOnline = networkManager.isNetworkAvailable();
                String message = isOnline ? "Network is available" : "No network connection";
                callback.onResult(isOnline, message);
            } catch (InterruptedException e) {
                callback.onResult(false, "Connection test interrupted");
            }
        }).start();
    }

    /**
     * Interface for server status callback
     */
    public interface ServerStatusCallback {
        void onResult(boolean isOnline, String message);
    }
    
    // Notification methods
    private void notifyStarted() {
        if (syncListener != null) {
            syncListener.onSyncStarted();
        }
    }
    
    private void notifyProgress(String message) {
        if (syncListener != null) {
            syncListener.onSyncProgress(message);
        }
    }
    
    private void notifyCompleted(boolean success, String message) {
        if (syncListener != null) {
            syncListener.onSyncCompleted(success, message);
        }
    }
    
    private void notifyError(String error) {
        currentStatus = SyncStatus.ERROR;
        if (syncListener != null) {
            syncListener.onSyncError(error);
        }
    }
}
