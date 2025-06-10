package com.example.student3.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.student3.dao.AnnouncementDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Announcement;
import com.example.student3.utils.NotificationHelper;

import java.util.List;

public class AnnouncementRepository {
    private final AnnouncementDao announcementDao;
    private final LiveData<List<Announcement>> allAnnouncements;
    private final NotificationHelper notificationHelper;

    public AnnouncementRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        announcementDao = db.announcementDao();
        allAnnouncements = announcementDao.getAllAnnouncements();
        notificationHelper = new NotificationHelper(application);
    }

    public LiveData<List<Announcement>> getAllAnnouncements() {
        return allAnnouncements;
    }

    public LiveData<Announcement> getAnnouncementById(int announcementId) {
        return announcementDao.getAnnouncementById(announcementId);
    }

    public LiveData<List<Announcement>> getImportantAnnouncements() {
        return announcementDao.getImportantAnnouncements();
    }

    public LiveData<List<Announcement>> searchAnnouncements(String query) {
        return announcementDao.searchAnnouncements(query);
    }

    public LiveData<List<Announcement>> getUnreadAnnouncements() {
        return announcementDao.getUnreadAnnouncements();
    }

    public LiveData<Integer> getUnreadAnnouncementCount() {
        return announcementDao.getUnreadAnnouncementCount();
    }

    public void insert(Announcement announcement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = announcementDao.insert(announcement);
            // Trigger notification for new announcement
            if (id > 0) {
                announcement.setAnnouncementId((int) id);
                notificationHelper.showAnnouncementNotification(announcement);
            }
        });
    }

    public void update(Announcement announcement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            announcementDao.update(announcement);
        });
    }

    public void delete(Announcement announcement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            announcementDao.delete(announcement);
        });
    }

    public void markAsRead(int announcementId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            announcementDao.markAsRead(announcementId);
        });
    }

    public void markAsUnread(int announcementId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            announcementDao.markAsUnread(announcementId);
        });
    }
}
