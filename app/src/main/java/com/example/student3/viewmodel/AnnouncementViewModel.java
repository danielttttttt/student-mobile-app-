package com.example.student3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.Announcement;
import com.example.student3.repository.AnnouncementRepository;

import java.util.List;

public class AnnouncementViewModel extends AndroidViewModel {
    private final AnnouncementRepository repository;
    private final LiveData<List<Announcement>> allAnnouncements;

    public AnnouncementViewModel(Application application) {
        super(application);
        repository = new AnnouncementRepository(application);
        allAnnouncements = repository.getAllAnnouncements();
    }

    public LiveData<List<Announcement>> getAllAnnouncements() {
        return allAnnouncements;
    }

    public LiveData<Announcement> getAnnouncementById(int announcementId) {
        return repository.getAnnouncementById(announcementId);
    }

    public LiveData<List<Announcement>> getImportantAnnouncements() {
        return repository.getImportantAnnouncements();
    }

    public LiveData<List<Announcement>> searchAnnouncements(String query) {
        return repository.searchAnnouncements(query);
    }

    public LiveData<List<Announcement>> getUnreadAnnouncements() {
        return repository.getUnreadAnnouncements();
    }

    public LiveData<Integer> getUnreadAnnouncementCount() {
        return repository.getUnreadAnnouncementCount();
    }

    public void insert(Announcement announcement) {
        repository.insert(announcement);
    }

    public void update(Announcement announcement) {
        repository.update(announcement);
    }

    public void delete(Announcement announcement) {
        repository.delete(announcement);
    }

    public void markAsRead(int announcementId) {
        repository.markAsRead(announcementId);
    }

    public void markAsUnread(int announcementId) {
        repository.markAsUnread(announcementId);
    }
}
