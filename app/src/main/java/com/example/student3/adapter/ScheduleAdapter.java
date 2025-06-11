package com.example.student3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student3.R;
import com.example.student3.model.Registration;
import com.example.student3.model.Course;
import com.example.student3.utils.ScheduleUtils;

import java.util.List;
import java.util.Map;

/**
 * ScheduleAdapter displays student's enrolled courses in a schedule format.
 * 
 * Features:
 * - Shows course information with time slots
 * - Displays registration status
 * - Color-coded status indicators
 * - Click handling for course details
 * 
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2025
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Registration> registrations;
    private Map<Integer, Course> courseMap; // Map courseId to Course object
    private OnScheduleItemClickListener listener;

    public interface OnScheduleItemClickListener {
        void onScheduleItemClick(Registration registration);
    }

    public ScheduleAdapter(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public ScheduleAdapter(List<Registration> registrations, Map<Integer, Course> courseMap) {
        this.registrations = registrations;
        this.courseMap = courseMap;
    }

    public ScheduleAdapter(List<Registration> registrations, OnScheduleItemClickListener listener) {
        this.registrations = registrations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Registration registration = registrations.get(position);
        holder.bind(registration);
    }

    @Override
    public int getItemCount() {
        return registrations != null ? registrations.size() : 0;
    }

    public void updateSchedule(List<Registration> newRegistrations) {
        this.registrations = newRegistrations;
        notifyDataSetChanged();
    }

    public void updateSchedule(List<Registration> newRegistrations, Map<Integer, Course> newCourseMap) {
        this.registrations = newRegistrations;
        this.courseMap = newCourseMap;
        notifyDataSetChanged();
    }

    public void setCourseMap(Map<Integer, Course> courseMap) {
        this.courseMap = courseMap;
        notifyDataSetChanged();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCourseCode;
        private final TextView tvCourseTitle;
        private final TextView tvTimeSlot;
        private final TextView tvStatus;
        private final TextView tvGrade;
        private final View statusIndicator;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tv_course_code);
            tvCourseTitle = itemView.findViewById(R.id.tv_course_title);
            tvTimeSlot = itemView.findViewById(R.id.tv_time_slot);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvGrade = itemView.findViewById(R.id.tv_grade);
            statusIndicator = itemView.findViewById(R.id.status_indicator);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onScheduleItemClick(registrations.get(position));
                    }
                }
            });
        }

        public void bind(Registration registration) {
            Course course = null;
            if (courseMap != null) {
                course = courseMap.get(registration.getCourseId());
            }

            if (course != null) {
                // Display course information with scheduling
                tvCourseCode.setText(course.getCourseCode());
                tvCourseTitle.setText(course.getTitle());

                // Format and display schedule information
                String scheduleText = ScheduleUtils.formatSchedule(
                    course.getDaysOfWeek(),
                    course.getStartPeriod(),
                    course.getEndPeriod()
                );
                tvTimeSlot.setText(scheduleText);
            } else {
                // Fallback to basic registration info
                tvCourseCode.setText("COURSE-" + registration.getCourseId());
                tvCourseTitle.setText("Course Title");
                tvTimeSlot.setText("Schedule TBD");
            }

            tvStatus.setText(registration.getStatus());

            // Show grade if available
            if (registration.getGrade() != null && !registration.getGrade().isEmpty()) {
                tvGrade.setVisibility(View.VISIBLE);
                tvGrade.setText("Grade: " + registration.getGrade());
            } else {
                tvGrade.setVisibility(View.GONE);
            }

            // Set status indicator color
            setStatusIndicator(registration.getStatus());
        }

        private void setStatusIndicator(String status) {
            int colorRes;
            switch (status) {
                case "REGISTERED":
                    colorRes = R.color.success;
                    break;
                case "COMPLETED":
                    colorRes = R.color.primary;
                    break;
                case "DROPPED":
                    colorRes = R.color.error;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            statusIndicator.setBackgroundColor(
                itemView.getContext().getResources().getColor(colorRes, null)
            );
        }
    }
}
