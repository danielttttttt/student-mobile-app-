package com.example.student3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student3.R;
import com.example.student3.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseSimpleAdapter extends RecyclerView.Adapter<CourseSimpleAdapter.ViewHolder> {
    private List<Course> courses;
    private OnCourseClickListener onCourseClickListener;

    public CourseSimpleAdapter(List<Course> courses) {
        this.courses = courses;
    }

    public void setOnCourseClickListener(OnCourseClickListener listener) {
        this.onCourseClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course, onCourseClickListener);
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    public void updateCourses(List<Course> newCourses) {
        this.courses = newCourses != null ? newCourses : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCourseCode;
        private final TextView tvCourseTitle;
        private final TextView tvCourseDescription;
        private final TextView tvCreditHours;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tv_course_code);
            tvCourseTitle = itemView.findViewById(R.id.tv_course_title);
            tvCourseDescription = itemView.findViewById(R.id.tv_course_description);
            tvCreditHours = itemView.findViewById(R.id.tv_credit_hours);
        }

        public void bind(Course course, OnCourseClickListener listener) {
            tvCourseCode.setText(course.getCourseCode());
            tvCourseTitle.setText(course.getTitle());
            tvCourseDescription.setText(course.getDescription());
            tvCreditHours.setText(course.getCreditHours() + " Credits");

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCourseClick(course);
                }
            });
        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }
}
