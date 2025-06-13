package com.example.student3.network.models;

import com.example.student3.model.Announcement;
import com.example.student3.model.Course;
import com.example.student3.model.SimpleTodo;
import com.example.student3.model.UserProfile;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Sync response model for bulk synchronization
 * 
 * This class contains all server data that needs to be synchronized
 * with the local database, including updated announcements, courses, etc.
 */
public class SyncResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("sync_timestamp")
    private String syncTimestamp;
    
    @SerializedName("announcements")
    private List<Announcement> announcements;
    
    @SerializedName("courses")
    private List<Course> courses;
    
    @SerializedName("todos")
    private List<SimpleTodo> todos;
    
    @SerializedName("user_profile")
    private UserProfile userProfile;
    
    @SerializedName("conflicts")
    private List<SyncConflict> conflicts;
    
    @SerializedName("server_changes_count")
    private int serverChangesCount;
    
    @SerializedName("client_changes_processed")
    private int clientChangesProcessed;
    
    // Default constructor
    public SyncResponse() {}
    
    // Constructor for successful sync
    public SyncResponse(boolean success, String message, String syncTimestamp) {
        this.success = success;
        this.message = message;
        this.syncTimestamp = syncTimestamp;
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getSyncTimestamp() {
        return syncTimestamp;
    }
    
    public void setSyncTimestamp(String syncTimestamp) {
        this.syncTimestamp = syncTimestamp;
    }
    
    public List<Announcement> getAnnouncements() {
        return announcements;
    }
    
    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
    
    public List<Course> getCourses() {
        return courses;
    }
    
    public void setCourses(List<Course> courses) {
        this.courses = courses;
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
    
    public List<SyncConflict> getConflicts() {
        return conflicts;
    }
    
    public void setConflicts(List<SyncConflict> conflicts) {
        this.conflicts = conflicts;
    }
    
    public int getServerChangesCount() {
        return serverChangesCount;
    }
    
    public void setServerChangesCount(int serverChangesCount) {
        this.serverChangesCount = serverChangesCount;
    }
    
    public int getClientChangesProcessed() {
        return clientChangesProcessed;
    }
    
    public void setClientChangesProcessed(int clientChangesProcessed) {
        this.clientChangesProcessed = clientChangesProcessed;
    }
    
    /**
     * Check if sync has new announcements
     */
    public boolean hasAnnouncements() {
        return announcements != null && !announcements.isEmpty();
    }
    
    /**
     * Check if sync has course updates
     */
    public boolean hasCourseUpdates() {
        return courses != null && !courses.isEmpty();
    }
    
    /**
     * Check if sync has todo updates
     */
    public boolean hasTodoUpdates() {
        return todos != null && !todos.isEmpty();
    }
    
    /**
     * Check if sync has conflicts that need resolution
     */
    public boolean hasConflicts() {
        return conflicts != null && !conflicts.isEmpty();
    }
    
    /**
     * Get total number of changes from server
     */
    public int getTotalChanges() {
        int total = 0;
        if (announcements != null) total += announcements.size();
        if (courses != null) total += courses.size();
        if (todos != null) total += todos.size();
        return total;
    }
    
    @Override
    public String toString() {
        return "SyncResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", syncTimestamp='" + syncTimestamp + '\'' +
                ", announcementsCount=" + (announcements != null ? announcements.size() : 0) +
                ", coursesCount=" + (courses != null ? courses.size() : 0) +
                ", todosCount=" + (todos != null ? todos.size() : 0) +
                ", conflictsCount=" + (conflicts != null ? conflicts.size() : 0) +
                ", serverChangesCount=" + serverChangesCount +
                ", clientChangesProcessed=" + clientChangesProcessed +
                '}';
    }
    
    /**
     * Inner class for sync conflicts
     */
    public static class SyncConflict {
        @SerializedName("type")
        private String type; // "todo", "profile", etc.
        
        @SerializedName("local_id")
        private int localId;
        
        @SerializedName("server_data")
        private Object serverData;
        
        @SerializedName("local_data")
        private Object localData;
        
        @SerializedName("resolution_strategy")
        private String resolutionStrategy; // "server_wins", "client_wins", "merge"
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public int getLocalId() { return localId; }
        public void setLocalId(int localId) { this.localId = localId; }
        
        public Object getServerData() { return serverData; }
        public void setServerData(Object serverData) { this.serverData = serverData; }
        
        public Object getLocalData() { return localData; }
        public void setLocalData(Object localData) { this.localData = localData; }
        
        public String getResolutionStrategy() { return resolutionStrategy; }
        public void setResolutionStrategy(String resolutionStrategy) { this.resolutionStrategy = resolutionStrategy; }
    }
}
