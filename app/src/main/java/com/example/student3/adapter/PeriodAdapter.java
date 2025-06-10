package com.example.student3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student3.R;
import com.example.student3.model.Course;
import com.example.student3.utils.ScheduleUtils;

import java.util.ArrayList;
import java.util.List;

public class PeriodAdapter extends RecyclerView.Adapter<PeriodAdapter.PeriodViewHolder> {

    private List<Course> courses = new ArrayList<>();

    public void updateCourses(List<Course> newCourses) {
        this.courses.clear();
        if (newCourses != null) {
            this.courses.addAll(newCourses);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PeriodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_period, parent, false);
        return new PeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeriodViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class PeriodViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCourseCode;
        private final TextView tvCourseTitle;
        private final TextView tvPeriodTime;

        public PeriodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tv_period_course_code);
            tvCourseTitle = itemView.findViewById(R.id.tv_period_course_title);
            tvPeriodTime = itemView.findViewById(R.id.tv_period_time);
        }

        public void bind(Course course) {
            tvCourseCode.setText(course.getCourseCode());
            tvCourseTitle.setText(course.getTitle());
            
            // Format the time period
            String timeSlot = ScheduleUtils.getPeriodTimeRange(
                course.getStartPeriod(),
                course.getEndPeriod()
            );
            tvPeriodTime.setText(timeSlot);
        }
    }
}
