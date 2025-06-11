package com.example.student3.ui.announcements;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.student3.R;
import com.example.student3.databinding.FragmentAnnouncementDetailBinding;
import com.example.student3.viewmodel.AnnouncementViewModel;

/**
 * Fragment to display detailed view of an announcement
 * Shows full content, allows marking as read/unread, sharing, etc.
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Announcement Detail View
 * @since 2025
 */
public class AnnouncementDetailFragment extends Fragment {
    private static final String TAG = "AnnouncementDetailFragment";
    
    private FragmentAnnouncementDetailBinding binding;
    private AnnouncementViewModel announcementViewModel;
    
    private int announcementId;
    private String title;
    private String content;
    private String publishDate;
    private boolean isImportant;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        // Get arguments
        if (getArguments() != null) {
            announcementId = getArguments().getInt("announcementId", -1);
            title = getArguments().getString("title", "");
            content = getArguments().getString("content", "");
            publishDate = getArguments().getString("publishDate", "");
            isImportant = getArguments().getBoolean("isImportant", false);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnnouncementDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        announcementViewModel = new ViewModelProvider(this).get(AnnouncementViewModel.class);
        
        // Display announcement content
        displayAnnouncementContent();
        
        // Mark as read when viewed
        if (announcementId != -1) {
            announcementViewModel.markAsRead(announcementId);
        }
        
        // Setup click listeners
        setupClickListeners();
    }
    
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.announcement_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_share) {
            shareAnnouncement();
            return true;
        } else if (id == R.id.action_copy) {
            copyAnnouncementText();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void displayAnnouncementContent() {
        binding.textViewTitle.setText(title);
        binding.textViewContent.setText(content);
        binding.textViewDate.setText(publishDate);
        
        // Show/hide important badge
        if (isImportant) {
            binding.textViewImportantBadge.setVisibility(View.VISIBLE);
        } else {
            binding.textViewImportantBadge.setVisibility(View.GONE);
        }
    }
    
    private void setupClickListeners() {
        // Share button
        binding.buttonShare.setOnClickListener(v -> shareAnnouncement());
        
        // Copy button
        binding.buttonCopy.setOnClickListener(v -> copyAnnouncementText());
    }
    
    private void shareAnnouncement() {
        String shareText = title + "\n\n" + content + "\n\nPublished: " + publishDate;
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        
        try {
            startActivity(Intent.createChooser(shareIntent, "Share Announcement"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to share announcement", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void copyAnnouncementText() {
        String copyText = title + "\n\n" + content;
        
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Announcement", copyText);
        clipboard.setPrimaryClip(clip);
        
        Toast.makeText(getContext(), "Announcement copied to clipboard", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
