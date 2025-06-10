package com.example.student3.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "announcements")
public class Announcement {
    @PrimaryKey(autoGenerate = true)
    private int announcementId;

    @NonNull
    private String title;

    @NonNull
    private String content;

    private String publishDate;
    private String expiryDate;
    private boolean isImportant;
    private boolean isRead;

    public Announcement(@NonNull String title, @NonNull String content, String publishDate,
                        String expiryDate, boolean isImportant) {
        this.title = title;
        this.content = content;
        this.publishDate = publishDate;
        this.expiryDate = expiryDate;
        this.isImportant = isImportant;
        this.isRead = false; // New announcements are unread by default
    }

    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}