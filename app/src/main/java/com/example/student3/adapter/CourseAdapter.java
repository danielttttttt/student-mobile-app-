package com.example.student3.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student3.model.Course;
import com.example.student3.databinding.ItemCourseBinding;
import com.example.student3.viewmodel.InstructorViewModel;
import com.example.student3.viewmodel.RegistrationViewModel;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courses;
    private final OnCourseClickListener listener;
    private final LifecycleOwner lifecycleOwner;
    private final InstructorViewModel instructorViewModel;
    private final RegistrationViewModel registrationViewModel;

    public CourseAdapter(List<Course> courses, OnCourseClickListener listener,
                        LifecycleOwner lifecycleOwner, ViewModelStoreOwner viewModelStoreOwner) {
        this.courses = courses;
        this.listener = listener;
        this.lifecycleOwner = lifecycleOwner;

        ViewModelProvider viewModelProvider = new ViewModelProvider(viewModelStoreOwner);
        this.instructorViewModel = viewModelProvider.get(InstructorViewModel.class);
        this.registrationViewModel = viewModelProvider.get(RegistrationViewModel.class);
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void updateCourses(List<Course> newCourses) {
        this.courses = newCourses;
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        public CourseViewHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCourseClick(courses.get(position));
                }
            });
        }

        public void bind(Course course) {
            binding.tvCourseCode.setText(course.getCourseCode());
            binding.tvCourseTitle.setText(course.getTitle());
            binding.tvCreditHours.setText(String.format("%d Credit Hours", course.getCreditHours()));

            // Load instructor name
            if (course.getInstructorId() != null) {
                instructorViewModel.getInstructorById(course.getInstructorId())
                    .observe(lifecycleOwner, instructor -> {
                        if (instructor != null) {
                            binding.tvInstructor.setText(instructor.getFullName());
                        } else {
                            binding.tvInstructor.setText("Unknown Instructor");
                        }
                    });
            } else {
                binding.tvInstructor.setText("No Instructor Assigned");
            }


        }
    }

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }
}