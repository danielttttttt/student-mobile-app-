package com.example.student3.ui.schedule;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.student3.R;
import com.example.student3.adapter.ScheduleAdapter;
import com.example.student3.adapter.PeriodAdapter;
import com.example.student3.databinding.FragmentScheduleBinding;
import com.example.student3.model.Registration;
import com.example.student3.model.Course;
import com.example.student3.utils.UserSession;
import com.example.student3.utils.ScheduleUtils;
import com.example.student3.viewmodel.RegistrationViewModel;
import com.example.student3.viewmodel.CourseViewModel;
import com.example.student3.viewmodel.InstructorViewModel;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ScheduleFragment displays the student's course schedule.
 *
 * Features:
 * - Shows enrolled courses with time slots
 * - Weekly calendar view
 * - Course conflict detection
 * - Quick access to course details
 *
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2025
 */
public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private RegistrationViewModel registrationViewModel;
    private CourseViewModel courseViewModel;
    private InstructorViewModel instructorViewModel;
    private ScheduleAdapter scheduleAdapter;
    private PeriodAdapter periodAdapter;
    private UserSession userSession;
    private Map<Integer, Course> courseMap = new HashMap<>();
    private Map<Integer, String> instructorMap = new HashMap<>();
    private List<Registration> allRegistrations = new ArrayList<>();
    private List<Registration> filteredRegistrations = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();
        setupRecyclerView();
        setupSearchAndFilter();
        setupTabs();
        setupCalendar();
        observeScheduleData();
    }

    private void initializeComponents() {
        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        instructorViewModel = new ViewModelProvider(this).get(InstructorViewModel.class);
        userSession = new UserSession(requireContext());
    }

    private void setupRecyclerView() {
        scheduleAdapter = new ScheduleAdapter(new ArrayList<>());
        binding.recyclerSchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerSchedule.setAdapter(scheduleAdapter);
    }

    private void setupSearchAndFilter() {
        // Setup search functionality
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSchedule(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterSchedule(String query) {
        if (query.isEmpty()) {
            filteredRegistrations = new ArrayList<>(allRegistrations);
        } else {
            filteredRegistrations = new ArrayList<>();
            for (Registration registration : allRegistrations) {
                Course course = courseMap.get(registration.getCourseId());
                if (course != null) {
                    String searchText = (course.getCourseCode() + " " + course.getTitle()).toLowerCase();
                    if (searchText.contains(query.toLowerCase())) {
                        filteredRegistrations.add(registration);
                    }
                }
            }
        }

        // Update adapter with filtered results
        scheduleAdapter.updateScheduleWithInstructors(filteredRegistrations, courseMap, instructorMap);
        updateScheduleSummary();
    }



    private void observeScheduleData() {
        if (userSession.isLoggedIn()) {
            int studentId = userSession.getCurrentUserId();

            binding.progressBar.setVisibility(View.VISIBLE);

            registrationViewModel.getRegistrationsByStudent(studentId).observe(getViewLifecycleOwner(), registrations -> {
                binding.progressBar.setVisibility(View.GONE);

                if (registrations != null && !registrations.isEmpty()) {
                    List<Registration> activeRegistrations = filterActiveRegistrations(registrations);

                    if (!activeRegistrations.isEmpty()) {
                        loadCoursesForRegistrations(activeRegistrations);
                        binding.recyclerSchedule.setVisibility(View.VISIBLE);
                        binding.tvNoSchedule.setVisibility(View.GONE);
                        binding.tvScheduleCount.setText(getString(R.string.enrolled_courses_count, activeRegistrations.size()));
                    } else {
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                }
            });
        } else {
            showLoginRequired();
        }
    }

    private List<Registration> filterActiveRegistrations(List<Registration> registrations) {
        List<Registration> activeRegistrations = new ArrayList<>();
        for (Registration registration : registrations) {
            if ("REGISTERED".equals(registration.getStatus()) || "COMPLETED".equals(registration.getStatus())) {
                activeRegistrations.add(registration);
            }
        }
        return activeRegistrations;
    }

    private void loadCoursesForRegistrations(List<Registration> registrations) {
        courseMap.clear();
        instructorMap.clear();
        allRegistrations = new ArrayList<>(registrations);
        filteredRegistrations = new ArrayList<>(registrations);

        for (Registration registration : registrations) {
            courseViewModel.getCourseById(registration.getCourseId()).observe(getViewLifecycleOwner(), course -> {
                if (course != null) {
                    courseMap.put(course.getCourseId(), course);

                    // Load instructor information if available
                    if (course.getInstructorId() != null) {
                        instructorViewModel.getInstructorById(course.getInstructorId())
                            .observe(getViewLifecycleOwner(), instructor -> {
                                if (instructor != null) {
                                    instructorMap.put(course.getInstructorId(), instructor.getFullName());
                                }
                                updateAdapterIfReady(registrations);
                            });
                    } else {
                        updateAdapterIfReady(registrations);
                    }
                }
            });
        }
    }

    private void updateAdapterIfReady(List<Registration> registrations) {
        // Update adapter when we have all courses loaded
        if (courseMap.size() == registrations.size()) {
            scheduleAdapter.updateScheduleWithInstructors(filteredRegistrations, courseMap, instructorMap);
            updateScheduleSummary();
            updateNextClassIndicator();
        }
    }

    private void updateScheduleSummary() {
        int totalCredits = 0;
        for (Registration registration : filteredRegistrations) {
            Course course = courseMap.get(registration.getCourseId());
            if (course != null) {
                totalCredits += course.getCreditHours();
            }
        }

        binding.tvScheduleCount.setText(getString(R.string.enrolled_courses_count, filteredRegistrations.size()));
        binding.tvTotalCredits.setText("Total Credits: " + totalCredits);
    }

    private void updateNextClassIndicator() {
        // Find next class for today
        Calendar now = Calendar.getInstance();
        int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        int currentHour = now.get(Calendar.HOUR_OF_DAY);

        Course nextCourse = null;
        int nextPeriod = Integer.MAX_VALUE;

        for (Registration registration : filteredRegistrations) {
            Course course = courseMap.get(registration.getCourseId());
            if (course != null && courseHasClassOnDay(course, currentDayOfWeek)) {
                if (course.getStartPeriod() > currentHour && course.getStartPeriod() < nextPeriod) {
                    nextCourse = course;
                    nextPeriod = course.getStartPeriod();
                }
            }
        }

        if (nextCourse != null) {
            String nextClassText = nextCourse.getCourseCode() + " at " +
                                 ScheduleUtils.formatPeriodTime(nextPeriod);
            binding.tvNextClass.setText(nextClassText);
        } else {
            binding.tvNextClass.setText("No classes today");
        }
    }

    private void showEmptyState() {
        binding.recyclerSchedule.setVisibility(View.GONE);
        binding.tvNoSchedule.setVisibility(View.VISIBLE);
        binding.tvScheduleCount.setText(getString(R.string.enrolled_courses_count, 0));
    }

    private void showLoginRequired() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerSchedule.setVisibility(View.GONE);
        binding.tvNoSchedule.setVisibility(View.VISIBLE);
        binding.tvNoSchedule.setText(getString(R.string.login_required));
        Toast.makeText(getContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // Schedule tab selected
                    binding.layoutScheduleSection.setVisibility(View.VISIBLE);
                    binding.layoutCalendarSection.setVisibility(View.GONE);
                } else if (position == 1) {
                    // Calendar tab selected
                    binding.layoutScheduleSection.setVisibility(View.GONE);
                    binding.layoutCalendarSection.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
    }

    private void setupCalendar() {
        // Set up calendar view
        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Handle date selection and show periods for that date
                showPeriodsForDate(year, month + 1, dayOfMonth);
            }
        });

        // Update current month display
        updateCurrentMonthDisplay();
    }

    private void updateCurrentMonthDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String currentMonth = monthFormat.format(calendar.getTime());
        binding.tvCurrentMonth.setText(currentMonth);
    }

    private void showPeriodsForDate(int year, int month, int dayOfMonth) {
        // Get day of week (1 = Sunday, 2 = Monday, etc.)
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, dayOfMonth);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Convert to our format (1 = Monday, 2 = Tuesday, etc.)
        String dayName = getDayName(dayOfWeek);

        // Format the selected date correctly
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MM/dd/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        binding.tvSelectedDate.setText(formattedDate);

        // Find courses that have classes on this day
        List<Course> coursesForDay = new ArrayList<>();
        for (Course course : courseMap.values()) {
            if (course != null && courseHasClassOnDay(course, dayOfWeek)) {
                coursesForDay.add(course);
            }
        }

        // Show the selected date info section
        binding.layoutSelectedDateInfo.setVisibility(View.VISIBLE);

        if (!coursesForDay.isEmpty()) {
            // Setup period adapter if not already done
            if (periodAdapter == null) {
                periodAdapter = new PeriodAdapter();
                binding.recyclerDatePeriods.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recyclerDatePeriods.setAdapter(periodAdapter);
            }

            // Update the adapter with courses for this day
            periodAdapter.updateCourses(coursesForDay);
            binding.recyclerDatePeriods.setVisibility(View.VISIBLE);
            binding.tvNoPeriods.setVisibility(View.GONE);
        } else {
            // No classes for this day
            binding.recyclerDatePeriods.setVisibility(View.GONE);
            binding.tvNoPeriods.setVisibility(View.VISIBLE);
        }

        // Hide the instruction text
        binding.tvCalendarInstruction.setVisibility(View.GONE);
    }

    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "Sunday";
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            default: return "Unknown";
        }
    }

    private boolean courseHasClassOnDay(Course course, int dayOfWeek) {
        String daysOfWeek = course.getDaysOfWeek();
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return false;
        }

        // Convert Calendar day to our day format
        // Calendar: SUNDAY=1, MONDAY=2, TUESDAY=3, WEDNESDAY=4, THURSDAY=5, FRIDAY=6, SATURDAY=7
        String dayCode = "";
        switch (dayOfWeek) {
            case Calendar.SUNDAY: dayCode = "SUN"; break;
            case Calendar.MONDAY: dayCode = "MON"; break;
            case Calendar.TUESDAY: dayCode = "TUE"; break;
            case Calendar.WEDNESDAY: dayCode = "WED"; break;
            case Calendar.THURSDAY: dayCode = "THU"; break;
            case Calendar.FRIDAY: dayCode = "FRI"; break;
            case Calendar.SATURDAY: dayCode = "SAT"; break;
        }

        // Check if the course's days of week contains this day
        // The course days are stored as comma-separated values like "TUE,THU"
        return daysOfWeek.toUpperCase().contains(dayCode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}