package com.example.student3.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.student3.R;
import com.example.student3.adapter.AnnouncementSimpleAdapter;
import com.example.student3.adapter.CourseSimpleAdapter;
import com.example.student3.databinding.FragmentDashboardBinding;
import com.example.student3.model.Course;
import com.example.student3.utils.UserSession;
import com.example.student3.viewmodel.AnnouncementViewModel;
import com.example.student3.viewmodel.CourseViewModel;

import java.util.ArrayList;

/**
 * DashboardFragment displays the main dashboard for students.
 *
 * Features:
 * - Welcome message with user's name
 * - Course statistics (user's registered courses only)
 * - Recent announcements with click navigation
 * - Recent courses with click navigation
 * - "View All" buttons for full lists
 *
 * Note: Admin features like "Total Students" are removed for user-only app
 *
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2025
 */
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private AnnouncementViewModel announcementViewModel;
    private CourseViewModel courseViewModel;
    private AnnouncementSimpleAdapter announcementAdapter;
    private CourseSimpleAdapter courseAdapter;
    private UserSession userSession;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModels();
        setupRecyclerViews();
        observeData();
    }

    private void initViewModels() {
        announcementViewModel = new ViewModelProvider(this).get(AnnouncementViewModel.class);
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        userSession = new UserSession(requireContext());
    }

    private void setupRecyclerViews() {
        // Setup announcements RecyclerView
        announcementAdapter = new AnnouncementSimpleAdapter(new ArrayList<>());
        // Set click listener to mark announcements as read when clicked
        announcementAdapter.setOnAnnouncementClickListener(announcement -> {
            // Mark announcement as read
            announcementViewModel.markAsRead(announcement.getAnnouncementId());
        });
        binding.recyclerAnnouncements.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerAnnouncements.setAdapter(announcementAdapter);

        // Setup courses RecyclerView with click listener
        courseAdapter = new CourseSimpleAdapter(new ArrayList<>());
        courseAdapter.setOnCourseClickListener(this::navigateToCourseDetail);
        binding.recyclerRecentCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerRecentCourses.setAdapter(courseAdapter);
    }

    private void observeData() {
        // Observe announcements
        announcementViewModel.getImportantAnnouncements().observe(getViewLifecycleOwner(), announcements -> {
            if (announcements != null && !announcements.isEmpty()) {
                // Show only first 3 announcements
                int maxItems = Math.min(announcements.size(), 3);
                announcementAdapter.updateAnnouncements(announcements.subList(0, maxItems));
                binding.recyclerAnnouncements.setVisibility(View.VISIBLE);
                binding.tvNoAnnouncements.setVisibility(View.GONE);
            } else {
                binding.recyclerAnnouncements.setVisibility(View.GONE);
                binding.tvNoAnnouncements.setVisibility(View.VISIBLE);
            }
        });

        // Observe unread announcement count
        announcementViewModel.getUnreadAnnouncementCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                // Show notification badge or update UI to indicate unread announcements
                // You can add a badge to the announcements section header
                binding.tvViewAllAnnouncements.setText("View All (" + count + " unread)");
            } else {
                binding.tvViewAllAnnouncements.setText("View All");
            }
        });

        // Observe registered courses for the current user (filtered by their department)
        if (userSession != null && userSession.isLoggedIn()) {
            int studentId = userSession.getCurrentUserId();
            int userDepartmentId = userSession.getCurrentUserDepartmentId();

            // Show only registered courses from user's department
            courseViewModel.getRegisteredCoursesByStudentAndDepartment(studentId, userDepartmentId)
                    .observe(getViewLifecycleOwner(), courses -> {
                if (courses != null && !courses.isEmpty()) {
                    // Show only first 3 registered courses
                    int maxItems = Math.min(courses.size(), 3);
                    courseAdapter.updateCourses(courses.subList(0, maxItems));
                    binding.recyclerRecentCourses.setVisibility(View.VISIBLE);
                    binding.tvNoCourses.setVisibility(View.GONE);

                    // Update registered courses count
                    binding.tvTotalCourses.setText(String.valueOf(courses.size()));
                } else {
                    binding.recyclerRecentCourses.setVisibility(View.GONE);
                    binding.tvNoCourses.setVisibility(View.VISIBLE);
                    binding.tvTotalCourses.setText("0");
                }
            });
        } else {
            // User not logged in - show empty state
            binding.recyclerRecentCourses.setVisibility(View.GONE);
            binding.tvNoCourses.setVisibility(View.VISIBLE);
            binding.tvTotalCourses.setText("0");
        }

        // Set up click listeners for navigation
        setupClickListeners();
    }



    /**
     * Sets up click listeners for navigation to full lists
     */
    private void setupClickListeners() {
        // Navigate to courses list when clicking "View All" button - show only registered courses
        binding.tvViewAllCourses.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("showRegisteredOnly", true);
            Navigation.findNavController(v).navigate(R.id.nav_courses, args);
        });

        // Navigate to courses list when clicking on courses RecyclerView - show only registered courses
        binding.recyclerRecentCourses.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("showRegisteredOnly", true);
            Navigation.findNavController(v).navigate(R.id.nav_courses, args);
        });

        // Navigate to announcements list when clicking "View All" announcements button
        binding.tvViewAllAnnouncements.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.action_nav_dashboard_to_announcementList));

        // Set up welcome message with user's name
        setupWelcomeMessage();
    }

    /**
     * Sets up the welcome message with the current user's name
     */
    private void setupWelcomeMessage() {
        if (userSession != null && userSession.isLoggedIn()) {
            String userName = userSession.getUserName();
            if (userName != null && !userName.isEmpty()) {
                String welcomeMessage = getString(R.string.welcome_message, userName);
                binding.tvWelcomeMessage.setText(welcomeMessage);
            }
        }
    }

    /**
     * Navigate to course detail page when a course item is clicked
     */
    private void navigateToCourseDetail(Course course) {
        Bundle args = new Bundle();
        args.putInt("courseId", course.getCourseId());
        Navigation.findNavController(requireView()).navigate(R.id.action_nav_dashboard_to_courseDetailFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}