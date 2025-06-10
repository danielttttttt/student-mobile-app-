package com.example.student3.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student3.model.Announcement;

import java.util.List;

@Dao
public interface AnnouncementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Announcement announcement);

    @Update
    void update(Announcement announcement);

    @Delete
    void delete(Announcement announcement);

    @Query("DELETE FROM announcements")
    void deleteAll();

    @Query("SELECT * FROM announcements WHERE announcementId = :id")
    LiveData<Announcement> getAnnouncementById(int id);

    @Query("SELECT * FROM announcements ORDER BY publishDate DESC")
    LiveData<List<Announcement>> getAllAnnouncements();

    @Query("SELECT * FROM announcements WHERE isImportant = 1 ORDER BY publishDate DESC")
    LiveData<List<Announcement>> getImportantAnnouncements();

    @Query("SELECT * FROM announcements WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    LiveData<List<Announcement>> searchAnnouncements(String searchQuery);

    @Query("SELECT * FROM announcements WHERE isRead = 0 ORDER BY publishDate DESC")
    LiveData<List<Announcement>> getUnreadAnnouncements();

    @Query("SELECT COUNT(*) FROM announcements WHERE isRead = 0")
    LiveData<Integer> getUnreadAnnouncementCount();

    @Query("UPDATE announcements SET isRead = 1 WHERE announcementId = :announcementId")
    void markAsRead(int announcementId);

    @Query("UPDATE announcements SET isRead = 0 WHERE announcementId = :announcementId")
    void markAsUnread(int announcementId);

    @Query("SELECT * FROM announcements ORDER BY publishDate DESC")
    List<Announcement> getAllAnnouncementsSync();

    @Query("SELECT * FROM announcements WHERE isRead = 0 ORDER BY publishDate DESC")
    List<Announcement> getUnreadAnnouncementsSync();
}