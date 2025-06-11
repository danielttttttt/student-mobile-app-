package com.example.student3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student3.R;
import com.example.student3.model.Announcement;

import java.util.List;

/**
 * Full-featured adapter for displaying announcements in a RecyclerView
 * Supports click listeners, read status indicators, and importance badges
 * 
 * @author DANN4 Development Team
 * @version 1.0 - Full Announcement Adapter
 * @since 2025
 */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private List<Announcement> announcements;
    private OnAnnouncementClickListener clickListener;

    public interface OnAnnouncementClickListener {
        void onAnnouncementClick(Announcement announcement);
        void onAnnouncementLongClick(Announcement announcement);
    }

    public AnnouncementAdapter(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public void setOnAnnouncementClickListener(OnAnnouncementClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.bind(announcement);
        
        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAnnouncementClick(announcement);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAnnouncementLongClick(announcement);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return announcements != null ? announcements.size() : 0;
    }

    public void updateAnnouncements(List<Announcement> newAnnouncements) {
        this.announcements = newAnnouncements;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView contentTextView;
        private final TextView dateTextView;
        private final TextView importantBadge;
        private final View unreadIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewAnnouncementTitle);
            contentTextView = itemView.findViewById(R.id.textViewAnnouncementContent);
            dateTextView = itemView.findViewById(R.id.textViewAnnouncementDate);
            importantBadge = itemView.findViewById(R.id.textViewImportantBadge);
            unreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
        }

        public void bind(Announcement announcement) {
            titleTextView.setText(announcement.getTitle());
            contentTextView.setText(announcement.getContent());
            dateTextView.setText(announcement.getPublishDate());
            
            // Show/hide important badge
            if (announcement.isImportant()) {
                importantBadge.setVisibility(View.VISIBLE);
                importantBadge.setText("IMPORTANT");
            } else {
                importantBadge.setVisibility(View.GONE);
            }
            
            // Show/hide unread indicator
            if (!announcement.isRead()) {
                unreadIndicator.setVisibility(View.VISIBLE);
                // Make text bold for unread announcements
                titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                unreadIndicator.setVisibility(View.GONE);
                titleTextView.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
            
            // Adjust opacity for read announcements
            float alpha = announcement.isRead() ? 0.7f : 1.0f;
            itemView.setAlpha(alpha);
        }
    }
}
