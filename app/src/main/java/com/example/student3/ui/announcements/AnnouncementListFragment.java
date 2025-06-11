package com.example.student3.ui.announcements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.student3.R;
import com.example.student3.adapter.AnnouncementAdapter;
import com.example.student3.databinding.FragmentAnnouncementListBinding;
import com.example.student3.model.Announcement;
import com.example.student3.viewmodel.AnnouncementViewModel;

import java.util.ArrayList;

/**
 * Fragment to display the full list of announcements
 * Allows users to view all announcements, mark them as read/unread, and see details
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Announcement List View
 * @since 2025
 */
public class AnnouncementListFragment extends Fragment {
    private static final String TAG = "AnnouncementListFragment";
    
    private FragmentAnnouncementListBinding binding;
    private AnnouncementViewModel announcementViewModel;
    private AnnouncementAdapter announcementAdapter;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnnouncementListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        announcementViewModel = new ViewModelProvider(this).get(AnnouncementViewModel.class);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup observers
        setupObservers();
        
        // Setup click listeners
        setupClickListeners();
    }
    
    private void setupRecyclerView() {
        announcementAdapter = new AnnouncementAdapter(new ArrayList<>());
        binding.recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewAnnouncements.setAdapter(announcementAdapter);
        
        // Set click listener for announcement items
        announcementAdapter.setOnAnnouncementClickListener(new AnnouncementAdapter.OnAnnouncementClickListener() {
            @Override
            public void onAnnouncementClick(Announcement announcement) {
                // Mark announcement as read when clicked
                announcementViewModel.markAsRead(announcement.getAnnouncementId());
                
                // Navigate to announcement detail
                navigateToAnnouncementDetail(announcement);
            }
            
            @Override
            public void onAnnouncementLongClick(Announcement announcement) {
                // Show options menu for long click
                showAnnouncementOptions(announcement);
            }
        });
    }
    
    private void setupObservers() {
        // Observe all announcements
        announcementViewModel.getAllAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
            if (announcements != null && !announcements.isEmpty()) {
                announcementAdapter.updateAnnouncements(announcements);
                binding.recyclerViewAnnouncements.setVisibility(View.VISIBLE);
                binding.textViewNoAnnouncements.setVisibility(View.GONE);
                
                // Update announcement count
                binding.textViewAnnouncementCount.setText(
                    getString(R.string.announcement_count, announcements.size())
                );
            } else {
                binding.recyclerViewAnnouncements.setVisibility(View.GONE);
                binding.textViewNoAnnouncements.setVisibility(View.VISIBLE);
                binding.textViewAnnouncementCount.setText(
                    getString(R.string.announcement_count, 0)
                );
            }
        });
        
        // Observe unread count
        announcementViewModel.getUnreadAnnouncementCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                binding.textViewUnreadCount.setText(
                    getString(R.string.unread_announcements, count)
                );
                binding.textViewUnreadCount.setVisibility(View.VISIBLE);
            } else {
                binding.textViewUnreadCount.setVisibility(View.GONE);
            }
        });
    }
    
    private void setupClickListeners() {
        // Mark all as read button
        binding.buttonMarkAllRead.setOnClickListener(v -> {
            markAllAnnouncementsAsRead();
        });
        
        // Filter buttons (if you want to add filtering)
        binding.chipAll.setOnClickListener(v -> showAllAnnouncements());
        binding.chipUnread.setOnClickListener(v -> showUnreadAnnouncements());
        binding.chipImportant.setOnClickListener(v -> showImportantAnnouncements());
    }
    
    private void navigateToAnnouncementDetail(Announcement announcement) {
        // Create bundle with announcement data
        Bundle args = new Bundle();
        args.putInt("announcementId", announcement.getAnnouncementId());
        args.putString("title", announcement.getTitle());
        args.putString("content", announcement.getContent());
        args.putString("publishDate", announcement.getPublishDate());
        args.putBoolean("isImportant", announcement.isImportant());
        
        // Navigate to detail fragment
        try {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_announcementList_to_announcementDetail, args);
        } catch (Exception e) {
            // Fallback: show announcement in a dialog or toast
            showAnnouncementInDialog(announcement);
        }
    }
    
    private void showAnnouncementInDialog(Announcement announcement) {
        // Simple fallback - show in toast for now
        // You can implement a proper dialog later
        String message = announcement.getTitle() + "\n\n" + announcement.getContent();
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
    
    private void showAnnouncementOptions(Announcement announcement) {
        // Show options like mark as read/unread, delete (if admin), etc.
        String[] options = {
            announcement.isRead() ? "Mark as Unread" : "Mark as Read",
            "Share",
            "Copy Text"
        };
        
        // For now, just toggle read status
        if (announcement.isRead()) {
            announcementViewModel.markAsUnread(announcement.getAnnouncementId());
            Toast.makeText(getContext(), "Marked as unread", Toast.LENGTH_SHORT).show();
        } else {
            announcementViewModel.markAsRead(announcement.getAnnouncementId());
            Toast.makeText(getContext(), "Marked as read", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void markAllAnnouncementsAsRead() {
        // Get current announcements and mark them all as read
        announcementViewModel.getAllAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
            if (announcements != null) {
                for (Announcement announcement : announcements) {
                    if (!announcement.isRead()) {
                        announcementViewModel.markAsRead(announcement.getAnnouncementId());
                    }
                }
                Toast.makeText(getContext(), "All announcements marked as read", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showAllAnnouncements() {
        binding.chipAll.setChecked(true);
        binding.chipUnread.setChecked(false);
        binding.chipImportant.setChecked(false);
        
        announcementViewModel.getAllAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
            if (announcements != null) {
                announcementAdapter.updateAnnouncements(announcements);
            }
        });
    }
    
    private void showUnreadAnnouncements() {
        binding.chipAll.setChecked(false);
        binding.chipUnread.setChecked(true);
        binding.chipImportant.setChecked(false);
        
        announcementViewModel.getUnreadAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
            if (announcements != null) {
                announcementAdapter.updateAnnouncements(announcements);
            }
        });
    }
    
    private void showImportantAnnouncements() {
        binding.chipAll.setChecked(false);
        binding.chipUnread.setChecked(false);
        binding.chipImportant.setChecked(true);
        
        announcementViewModel.getImportantAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
            if (announcements != null) {
                announcementAdapter.updateAnnouncements(announcements);
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
