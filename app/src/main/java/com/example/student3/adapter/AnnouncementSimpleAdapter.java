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

public class AnnouncementSimpleAdapter extends RecyclerView.Adapter<AnnouncementSimpleAdapter.ViewHolder> {
    private List<Announcement> announcements;
    private OnAnnouncementClickListener clickListener;

    public interface OnAnnouncementClickListener {
        void onAnnouncementClick(Announcement announcement);
    }

    public AnnouncementSimpleAdapter(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public void setOnAnnouncementClickListener(OnAnnouncementClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.bind(announcement);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAnnouncementClick(announcement);
            }
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
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvDate;
        private final TextView tvImportantBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_announcement_title);
            tvContent = itemView.findViewById(R.id.tv_announcement_content);
            tvDate = itemView.findViewById(R.id.tv_announcement_date);
            tvImportantBadge = itemView.findViewById(R.id.tv_important_badge);
        }

        public void bind(Announcement announcement) {
            tvTitle.setText(announcement.getTitle());
            tvContent.setText(announcement.getContent());
            tvDate.setText(announcement.getPublishDate());
            
            if (announcement.isImportant()) {
                tvImportantBadge.setVisibility(View.VISIBLE);
            } else {
                tvImportantBadge.setVisibility(View.GONE);
            }
        }
    }
}
