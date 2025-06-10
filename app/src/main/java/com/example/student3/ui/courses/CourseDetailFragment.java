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

import com.example.student3.R;
import com.example.student3.databinding.FragmentCourseDetailBinding;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Course;
import com.example.student3.model.Registration;
import com.example.student3.model.Student;
import com.example.student3.model.Instructor;
import com.example.student3.model.Department;
import com.example.student3.model.Semester;
import com.example.student3.utils.UserSession;
import com.example.student3.utils.ScheduleUtils;
import com.example.student3.viewmodel.CourseViewModel;
import com.example.student3.viewmodel.RegistrationViewModel;
import com.example.student3.viewmodel.InstructorViewModel;
import com.example.student3.viewmodel.DepartmentViewModel;
import com.example.student3.viewmodel.SemesterViewModel;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class CourseDetailFragment extends Fragment {

    private FragmentCourseDetailBinding binding;
    private CourseViewModel courseViewModel;
    private RegistrationViewModel registrationViewModel;
    private InstructorViewModel instructorViewModel;
    private DepartmentViewModel departmentViewModel;
    private SemesterViewModel semesterViewModel;
    private UserSession userSession;
    private int courseId;
    private Course currentCourse;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        instructorViewModel = new ViewModelProvider(this).get(InstructorViewModel.class);
        departmentViewModel = new ViewModelProvider(this).get(DepartmentViewModel.class);
        semesterViewModel = new ViewModelProvider(this).get(SemesterViewModel.class);
        userSession = new UserSession(requireContext());

        // Set up toolbar
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Get courseId from arguments
        Bundle args = getArguments();
        if (args != null) {
            courseId = args.getInt("courseId", -1);
            if (courseId != -1) {
                loadCourseDetails();
            } else {
                Toast.makeText(requireContext(), "Invalid Course ID", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        } else {
            Toast.makeText(requireContext(), "No course data provided", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }

        // Set up button click listeners
        binding.btnRegister.setOnClickListener(v -> {
            if (binding.btnRegister.getText().toString().equals("Drop Course")) {
                dropCourse();
            } else {
                registerForCourse();
            }
        });
    }

    private void loadCourseDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);
        courseViewModel.getCourseById(courseId).observe(getViewLifecycleOwner(), course -> {
            binding.progressBar.setVisibility(View.GONE);
            if (course != null) {
                currentCourse = course;
                updateUI(course);
            } else {
                Toast.makeText(requireContext(), R.string.error_loading_course, Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    private void registerForCourse() {
        if (!userSession.isLoggedIn()) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentCourse == null) {
            Toast.makeText(requireContext(), R.string.error_loading_course, Toast.LENGTH_SHORT).show();
            return;
        }

        int studentId = userSession.getCurrentUserId();

        // Check if already registered
        registrationViewModel.getRegistrationByStudentAndCourse(studentId, courseId)
                .observe(getViewLifecycleOwner(), existingRegistration -> {
                    if (existingRegistration != null && "REGISTERED".equals(existingRegistration.getStatus())) {
                        Toast.makeText(requireContext(), R.string.already_registered, Toast.LENGTH_SHORT).show();
                        binding.btnRegister.setText("Already Registered");
                        binding.btnRegister.setEnabled(false);
                        return;
                    }

                    // Check enrollment limits
                    checkEnrollmentLimits(studentId);
                });
    }

    private void checkEnrollmentLimits(int studentId) {
        // Proceed with registration directly (no enrollment limit check)
        performRegistration(studentId);
    }

    private void performRegistration(int studentId) {
        // Use the actual logged-in user's ID
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Registration registration = new Registration(studentId, courseId, currentDate, "REGISTERED", null);

        registrationViewModel.insert(registration);

        Toast.makeText(requireContext(),
            getString(R.string.registration_successful) + ": " + currentCourse.getTitle(),
            Toast.LENGTH_LONG).show();

        // Update button to show "Drop Course" after successful registration
        binding.btnRegister.setText("Drop Course");
        binding.btnRegister.setEnabled(true);
    }



    private void dropCourse() {
        if (!userSession.isLoggedIn()) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentCourse == null) {
            Toast.makeText(requireContext(), R.string.error_loading_course, Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the actual logged-in user's ID
        int studentId = userSession.getCurrentUserId();

        registrationViewModel.getRegistrationByStudentAndCourse(studentId, courseId)
                .observe(getViewLifecycleOwner(), existingRegistration -> {
                    if (existingRegistration != null) {
                        existingRegistration.setStatus("DROPPED");
                        registrationViewModel.update(existingRegistration);

                        Toast.makeText(requireContext(),
                            getString(R.string.drop_successful) + ": " + currentCourse.getTitle(),
                            Toast.LENGTH_LONG).show();

                        binding.btnRegister.setText("Register");
                        binding.btnRegister.setEnabled(true);
                    } else {
                        Toast.makeText(requireContext(), R.string.course_drop_failed, Toast.LENGTH_SHORT).show();
                        binding.btnRegister.setText("Drop Course");
                        binding.btnRegister.setEnabled(true);
                    }
                });
    }

    private void updateUI(Course course) {
        binding.tvCourseCode.setText(course.getCourseCode());
        binding.tvCourseTitle.setText(course.getTitle());
        binding.tvCourseDescription.setText(course.getDescription());
        binding.tvCreditHours.setText(String.valueOf(course.getCreditHours()));

        // Load instructor name
        loadInstructorName(course.getInstructorId());

        // Load department name
        loadDepartmentName(course.getDepartmentId());

        // Load semester name
        loadSemesterName(course.getSemesterId());

        // Display scheduling information
        displayScheduleInfo(course);

        // Check registration status
        checkRegistrationStatus();
    }

    private void loadInstructorName(Integer instructorId) {
        if (instructorId == null) {
            binding.tvInstructor.setText("No Instructor Assigned");
            return;
        }
        instructorViewModel.getInstructorById(instructorId).observe(getViewLifecycleOwner(), instructor -> {
            if (instructor != null) {
                binding.tvInstructor.setText(instructor.getFullName());
            } else {
                binding.tvInstructor.setText("Unknown Instructor");
            }
        });
    }

    private void loadDepartmentName(Integer departmentId) {
        if (departmentId == null) {
            binding.tvDepartment.setText("No Department Assigned");
            return;
        }
        departmentViewModel.getDepartmentById(departmentId).observe(getViewLifecycleOwner(), department -> {
            if (department != null) {
                binding.tvDepartment.setText(department.getName());
            } else {
                binding.tvDepartment.setText("Unknown Department");
            }
        });
    }

    private void loadSemesterName(Integer semesterId) {
        if (semesterId == null) {
            binding.tvSemester.setText("No Semester Assigned");
            return;
        }
        semesterViewModel.getSemesterById(semesterId).observe(getViewLifecycleOwner(), semester -> {
            if (semester != null) {
                binding.tvSemester.setText(semester.getName());
            } else {
                binding.tvSemester.setText("Unknown Semester");
            }
        });
    }

    private void checkRegistrationStatus() {
        if (userSession.isLoggedIn()) {
            // Use the actual logged-in user's ID
            int studentId = userSession.getCurrentUserId();
            registrationViewModel.getRegistrationByStudentAndCourse(studentId, courseId)
                    .observe(getViewLifecycleOwner(), registration -> {
                        if (registration != null && "REGISTERED".equals(registration.getStatus())) {
                            binding.btnRegister.setText("Drop Course");
                            binding.btnRegister.setEnabled(true);
                        } else {
                            binding.btnRegister.setText("Register");
                            binding.btnRegister.setEnabled(true);
                        }
                    });
        } else {
            binding.btnRegister.setText("Login Required");
            binding.btnRegister.setEnabled(false);
        }
    }

    private void displayScheduleInfo(Course course) {
        // Display schedule using ScheduleUtils
        String scheduleText = ScheduleUtils.formatSchedule(
            course.getDaysOfWeek(),
            course.getStartPeriod(),
            course.getEndPeriod()
        );
        binding.tvSchedule.setText(scheduleText);

        // Display start date
        String startDate = course.getStartDate();
        if (startDate != null && !startDate.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(startDate);
                if (date != null) {
                    binding.tvStartDate.setText(outputFormat.format(date));
                } else {
                    binding.tvStartDate.setText(startDate);
                }
            } catch (ParseException e) {
                binding.tvStartDate.setText(startDate);
            }
        } else {
            binding.tvStartDate.setText("TBD");
        }

        // Display end date
        String endDate = course.getEndDate();
        if (endDate != null && !endDate.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(endDate);
                if (date != null) {
                    binding.tvEndDate.setText(outputFormat.format(date));
                } else {
                    binding.tvEndDate.setText(endDate);
                }
            } catch (ParseException e) {
                binding.tvEndDate.setText(endDate);
            }
        } else {
            binding.tvEndDate.setText("TBD");
        }

        // Display weekly hours
        int weeklyHours = course.getTotalWeeklyHours();
        String hoursText = weeklyHours + " hour" + (weeklyHours != 1 ? "s" : "") + " per week";
        binding.tvWeeklyHours.setText(hoursText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
