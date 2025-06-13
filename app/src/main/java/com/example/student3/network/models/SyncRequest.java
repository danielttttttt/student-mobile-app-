package com.example.student3.network.models;

import com.example.student3.model.SimpleTodo;
import com.example.student3.model.UserProfile;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Sync request model for bulk synchronization
 * 
 * This class contains all local data that needs to be synchronized
 * with the server, including todos, profile updates, and timestamps.
 */
public class SyncRequest {
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("last_sync_timestamp")
    private String lastSyncTimestamp;
    
    @SerializedName("device_id")
    private String deviceId;
    
    @SerializedName("todos")
    private List<SimpleTodo> todos;
    
    @SerializedName("user_profile")
    private UserProfile userProfile;
    
    @SerializedName("deleted_todo_ids")
    private List<Integer> deletedTodoIds;
    
    @SerializedName("app_version")
    private String appVersion;
    
    // Default constructor
    public SyncRequest() {}
    
    // Constructor with essential fields
    public SyncRequest(int userId, String lastSyncTimestamp, String deviceId) {
        this.userId = userId;
        this.lastSyncTimestamp = lastSyncTimestamp;
        this.deviceId = deviceId;
    }
    
    // Getters and setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getLastSyncTimestamp() {
        return lastSyncTimestamp;
    }
    
    public void setLastSyncTimestamp(String lastSyncTimestamp) {
        this.lastSyncTimestamp = lastSyncTimestamp;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public List<SimpleTodo> getTodos() {
        return todos;
    }
    
    public void setTodos(List<SimpleTodo> todos) {
        this.todos = todos;
    }
    
    public UserProfile getUserProfile() {
        return userProfile;
    }
    
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
    
    public List<Integer> getDeletedTodoIds() {
        return deletedTodoIds;
    }
    
    public void setDeletedTodoIds(List<Integer> deletedTodoIds) {
        this.deletedTodoIds = deletedTodoIds;
    }
    
    public String getAppVersion() {
        return appVersion;
    }
    
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    
    /**
     * Check if request has todos to sync
     */
    public boolean hasTodos() {
        return todos != null && !todos.isEmpty();
    }
    
    /**
     * Check if request has profile updates
     */
    public boolean hasProfileUpdates() {
        return userProfile != null;
    }
    
    /**
     * Check if request has deleted items
     */
    public boolean hasDeletedItems() {
        return deletedTodoIds != null && !deletedTodoIds.isEmpty();
    }
    
    @Override
    public String toString() {
        return "SyncRequest{" +
                "userId=" + userId +
                ", lastSyncTimestamp='" + lastSyncTimestamp + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", todosCount=" + (todos != null ? todos.size() : 0) +
                ", hasProfile=" + (userProfile != null) +
                ", deletedCount=" + (deletedTodoIds != null ? deletedTodoIds.size() : 0) +
                ", appVersion='" + appVersion + '\'' +
                '}';
    }
}
