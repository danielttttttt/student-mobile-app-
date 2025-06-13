package com.example.student3.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.student3.dao.AnnouncementDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Announcement;
import com.example.student3.network.NetworkManager;
import com.example.student3.utils.NotificationHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementRepository {
    private static final String TAG = "AnnouncementRepository";

    private final AnnouncementDao announcementDao;
    private final LiveData<List<Announcement>> allAnnouncements;
    private final NotificationHelper notificationHelper;
    private final NetworkManager networkManager;
    private final MutableLiveData<Boolean> syncStatus = new MutableLiveData<>(false);

    public AnnouncementRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        announcementDao = db.announcementDao();
        allAnnouncements = announcementDao.getAllAnnouncements();
        notificationHelper = new NotificationHelper(application);
        networkManager = NetworkManager.getInstance(application);
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

    // ========== NETWORK FUNCTIONALITY ==========

    /**
     * Get sync status LiveData for UI observation
     */
    public LiveData<Boolean> getSyncStatus() {
        return syncStatus;
    }

    /**
     * Sync announcements from server
     * This demonstrates how network sync would work
     */
    public void syncAnnouncementsFromServer() {
        if (!networkManager.isNetworkAvailable()) {
            Log.w(TAG, "No network available for sync");
            syncStatus.postValue(false);
            return;
        }

        Log.d(TAG, "Starting announcement sync from server");
        syncStatus.postValue(true); // Indicate sync in progress

        // Call API to get announcements
        NetworkManager.getApiService().getAnnouncements().enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Successfully received " + response.body().size() + " announcements from server");

                    // Process server announcements
                    processServerAnnouncements(response.body());
                } else {
                    Log.e(TAG, "Server response error: " + response.code());
                    syncStatus.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Log.e(TAG, "Network error during announcement sync", t);
                syncStatus.postValue(false);
            }
        });
    }

    /**
     * Process announcements received from server
     * In a real app, this would merge server data with local data
     */
    private void processServerAnnouncements(List<Announcement> serverAnnouncements) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // For demo purposes, we'll just log the server data
                // In a real implementation, you would:
                // 1. Compare server announcements with local ones
                // 2. Insert new announcements
                // 3. Update existing ones if modified
                // 4. Handle conflicts appropriately

                Log.d(TAG, "Processing server announcements:");
                for (Announcement announcement : serverAnnouncements) {
                    Log.d(TAG, "Server announcement: " + announcement.getTitle());

                    // Example: Check if announcement exists locally
                    // If not, insert it (this is just a demo)
                    // In real implementation, you'd have proper ID mapping
                }

                Log.d(TAG, "Announcement sync completed successfully");
                syncStatus.postValue(false); // Sync completed

            } catch (Exception e) {
                Log.e(TAG, "Error processing server announcements", e);
                syncStatus.postValue(false);
            }
        });
    }

    /**
     * Check if network sync is available
     */
    public boolean isNetworkSyncAvailable() {
        return networkManager.isNetworkAvailable();
    }

    /**
     * Get network status description
     */
    public String getNetworkStatus() {
        return networkManager.getNetworkStatusDescription();
    }
}
