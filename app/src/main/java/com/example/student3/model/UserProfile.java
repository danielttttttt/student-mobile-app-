package com.example.student3.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profiles",
        foreignKeys = @ForeignKey(entity = Student.class, parentColumns = "studentId", childColumns = "userId", onDelete = ForeignKey.CASCADE),
        indices = @Index("userId"))
public class UserProfile {
    @PrimaryKey(autoGenerate = true)
    private int profileId;

    private int userId;
    private String languagePreference;
    private String themePreference;
    private boolean notificationEnabled;
    private String lastLogin;

    public UserProfile(int userId, String languagePreference, String themePreference,
                       boolean notificationEnabled, String lastLogin) {
        this.userId = userId;
        this.languagePreference = languagePreference;
        this.themePreference = themePreference;
        this.notificationEnabled = notificationEnabled;
        this.lastLogin = lastLogin;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public String getThemePreference() {
        return themePreference;
    }

    public void setThemePreference(String themePreference) {
        this.themePreference = themePreference;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
}