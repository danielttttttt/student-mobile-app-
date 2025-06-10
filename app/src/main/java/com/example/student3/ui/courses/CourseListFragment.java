package com.example.student3.ui.courses;

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
import com.example.student3.adapter.CourseAdapter;
import com.example.student3.databinding.FragmentCourseListBinding;
import com.example.student3.model.Course;
import com.example.student3.model.Department;
import com.example.student3.utils.UserSession;
import com.example.student3.viewmodel.CourseViewModel;
import com.example.student3.viewmodel.DepartmentViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying and managing the list of available courses.
 *
 * Features:
 * - Course list display with department filtering
 * - Course search functionality
 * - Navigation to course details
 * - Loading states and error handling
 * - User-specific course display (registered vs all)
 *
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2024
 */
public class CourseListFragment extends Fragment implements CourseAdapter.OnCourseClickListener {

    private FragmentCourseListBinding binding;
    private CourseViewModel courseViewModel;
    private DepartmentViewModel departmentViewModel;
    private CourseAdapter adapter;
    private UserSession userSession;
    
    private boolean showRegisteredOnly = false;
    private int selectedDepartmentId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();
        setupRecyclerView();
        setupDepartmentChips();
        setupSearch();
        setupToggleButton();
        observeCourses();
    }

    private void initViewModels() {
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        departmentViewModel = new ViewModelProvider(this).get(DepartmentViewModel.class);
        userSession = new UserSession(requireContext());
    }

    private void setupRecyclerView() {
        adapter = new CourseAdapter(new ArrayList<>(), this, this, this);
        binding.recyclerCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerCourses.setAdapter(adapter);
    }

    private void setupDepartmentChips() {
        // Hide department chips since users should only see their own department courses
        binding.chipGroupDepartments.setVisibility(View.GONE);

        // Set the selected department to the current user's department
        if (userSession.isLoggedIn()) {
            selectedDepartmentId = userSession.getCurrentUserDepartmentId();
        }
    }

    private void setupSearch() {
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = binding.etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchCourses(query);
                return true;
            }
            return false;
        });
        
        binding.tilSearch.setEndIconOnClickListener(v -> {
            String query = binding.etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchCourses(query);
            } else {
                loadCourses();
            }
        });
    }

    private void setupToggleButton() {
        updateToggleButtonText();
        binding.btnToggleRegistered.setOnClickListener(v -> {
            showRegisteredOnly = !showRegisteredOnly;
            updateToggleButtonText();
            loadCourses();
        });
    }

    private void updateToggleButtonText() {
        if (showRegisteredOnly) {
            binding.btnToggleRegistered.setText(R.string.show_all_courses);
        } else {
            binding.btnToggleRegistered.setText(R.string.show_registered_courses);
        }
    }

    private void observeCourses() {
        loadCourses();
    }

    private void loadCourses() {
        binding.progressBar.setVisibility(View.VISIBLE);

        if (!userSession.isLoggedIn()) {
            // If user is not logged in, show all courses
            courseViewModel.getAllCourses()
                    .observe(getViewLifecycleOwner(), this::handleCoursesResult);
            return;
        }

        int userDepartmentId = userSession.getCurrentUserDepartmentId();
        int studentId = userSession.getCurrentUserId();

        if (showRegisteredOnly) {
            // Show only registered courses from user's department
            courseViewModel.getRegisteredCoursesByStudentAndDepartment(studentId, userDepartmentId)
                    .observe(getViewLifecycleOwner(), this::handleCoursesResult);
        } else {
            // Show all courses from user's department, or all courses if department ID is invalid
            if (userDepartmentId > 0) {
                courseViewModel.getCoursesByDepartment(userDepartmentId)
                        .observe(getViewLifecycleOwner(), this::handleCoursesResult);
            } else {
                // Fallback to showing all courses if department ID is invalid
                courseViewModel.getAllCourses()
                        .observe(getViewLifecycleOwner(), this::handleCoursesResult);
            }
        }
    }

    private void handleCoursesResult(List<Course> courses) {
        binding.progressBar.setVisibility(View.GONE);
        if (courses != null && !courses.isEmpty()) {
            adapter.updateCourses(courses);
            binding.tvNoCourses.setVisibility(View.GONE);
            binding.recyclerCourses.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoCourses.setVisibility(View.VISIBLE);
            binding.recyclerCourses.setVisibility(View.GONE);
        }
    }

    private void searchCourses(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);

        if (!userSession.isLoggedIn()) {
            handleCoursesResult(new ArrayList<>());
            return;
        }

        int userDepartmentId = userSession.getCurrentUserDepartmentId();
        // Search only within user's department
        courseViewModel.searchCoursesByDepartment(query, userDepartmentId)
                .observe(getViewLifecycleOwner(), this::handleCoursesResult);
    }

    @Override
    public void onCourseClick(Course course) {
        Bundle args = new Bundle();
        args.putInt("courseId", course.getCourseId());
        Navigation.findNavController(requireView()).navigate(R.id.action_nav_courses_to_courseDetailFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
