package com.example.student3.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.student3.R;
import com.example.student3.databinding.FragmentProfileBinding;
import com.example.student3.model.Student;
import com.example.student3.model.Department;
import com.example.student3.utils.UserSession;
import com.example.student3.viewmodel.StudentViewModel;
import com.example.student3.viewmodel.DepartmentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ProfileFragment displays and manages the current user's profile information.
 *
 * Features:
 * - Display student profile information (name, email, phone, etc.)
 * - Profile image management with camera/gallery selection
 * - Permission handling for media access
 * - Image optimization and storage
 *
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2024
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private StudentViewModel studentViewModel;
    private DepartmentViewModel departmentViewModel;
    private UserSession userSession;
    private Student currentStudent;
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            String imagePath = saveImageToInternalStorage(selectedImageUri);
                            android.util.Log.d("ProfileFragment", "Image saved to: " + imagePath);

                            // Verify the file exists before proceeding
                            File savedImageFile = new File(imagePath);
                            if (!savedImageFile.exists()) {
                                throw new Exception("Saved image file does not exist: " + imagePath);
                            }

                            android.util.Log.d("ProfileFragment", "Saved file verified. Size: " + savedImageFile.length() + " bytes");

                            if (currentStudent != null) {
                                // Update the student object
                                currentStudent.setProfileImagePath(imagePath);
                                android.util.Log.d("ProfileFragment", "Setting profile image path: " + imagePath);

                                // Update database first
                                studentViewModel.update(currentStudent);
                                android.util.Log.d("ProfileFragment", "Updated student in database");

                                // Update UserSession completely to ensure synchronization
                                userSession.createLoginSession(currentStudent);
                                android.util.Log.d("ProfileFragment", "Updated UserSession with complete student data including image path");

                                // Load image from saved file path
                                Glide.with(this).load(savedImageFile).circleCrop().into(binding.ivProfileImage);
                                android.util.Log.d("ProfileFragment", "Image loaded in UI from: " + imagePath);

                                Toast.makeText(getContext(), R.string.profile_image_updated, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            android.util.Log.e("ProfileFragment", "Error saving image: " + e.getMessage(), e);
                            Toast.makeText(getContext(), getString(R.string.error_saving_image, e.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) openImagePicker();
                else Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        departmentViewModel = new ViewModelProvider(this).get(DepartmentViewModel.class);
        userSession = new UserSession(requireContext());
        loadStudentProfile(); // Fixed: Now uses UserSession for consistent data
        binding.btnChangePhoto.setOnClickListener(v -> checkPermissionAndOpenImagePicker());
        binding.btnEditProfile.setOnClickListener(v -> Toast.makeText(getContext(), R.string.edit_profile_clicked, Toast.LENGTH_SHORT).show());
    }

    private void loadStudentProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);

        // Use UserSession to get current user data
        if (userSession.isLoggedIn()) {
            // Get current user from UserSession
            Student student = userSession.getCurrentUser();
            android.util.Log.d("ProfileFragment", "Loading student profile. Student: " + (student != null ? student.getEmail() : "null"));

            if (student != null) {
                android.util.Log.d("ProfileFragment", "Student profile image path from UserSession: " + student.getProfileImagePath());
                currentStudent = student;
                binding.progressBar.setVisibility(View.GONE);
                updateUI(student);
            } else {
                // Handle case where user session exists but student data is null
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading user profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            // User is not logged in, show error or redirect to login
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Please log in to view profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(Student student) {
        android.util.Log.d("ProfileFragment", "Updating UI for student: " + student.getEmail());
        android.util.Log.d("ProfileFragment", "Student department ID: " + student.getDepartmentId());

        binding.tvStudentName.setText(student.getFullName());
        binding.tvStudentId.setText(String.valueOf(student.getStudentId()));
        binding.tvEmail.setText(student.getEmail());
        binding.tvPhone.setText(student.getPhone());
        binding.tvEnrollmentDate.setText(student.getEnrollmentDate());

        // Load department name with enhanced logging
        if (student.getDepartmentId() > 0) {
            android.util.Log.d("ProfileFragment", "Loading department with ID: " + student.getDepartmentId());
            departmentViewModel.getDepartmentById(student.getDepartmentId()).observe(getViewLifecycleOwner(), department -> {
                android.util.Log.d("ProfileFragment", "Department loaded: " + (department != null ? department.getName() : "null"));
                if (department != null) {
                    binding.tvDepartment.setText(department.getName());
                    android.util.Log.d("ProfileFragment", "Department name set to: " + department.getName());
                } else {
                    binding.tvDepartment.setText("Unknown Department");
                    android.util.Log.w("ProfileFragment", "Department not found for ID: " + student.getDepartmentId());
                }
            });
        } else {
            binding.tvDepartment.setText("No Department");
            android.util.Log.w("ProfileFragment", "Student has no department ID (ID: " + student.getDepartmentId() + ")");
        }

        // Load profile image
        String profileImagePath = student.getProfileImagePath();
        android.util.Log.d("ProfileFragment", "Loading profile image. Path: " + profileImagePath);

        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            File imageFile = new File(profileImagePath);
            android.util.Log.d("ProfileFragment", "Image file exists: " + imageFile.exists() + ", Path: " + imageFile.getAbsolutePath());

            if (imageFile.exists()) {
                Glide.with(this).load(imageFile).circleCrop().into(binding.ivProfileImage);
                android.util.Log.d("ProfileFragment", "Loading image from file: " + profileImagePath);
            } else {
                android.util.Log.w("ProfileFragment", "Image file does not exist: " + profileImagePath);
                binding.ivProfileImage.setImageResource(R.drawable.ic_profile);
            }
        } else {
            android.util.Log.d("ProfileFragment", "No profile image path found, using default image");
            binding.ivProfileImage.setImageResource(R.drawable.ic_profile);
        }
    }

    private void checkPermissionAndOpenImagePicker() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(Uri imageUri) throws Exception {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
        if (inputStream == null) throw new Exception("Cannot open input stream");

        // Use a fixed filename to avoid creating multiple files
        String imageFileName = "profile_image_" + userSession.getCurrentUserId() + ".jpg";
        File storageDir = requireContext().getFilesDir();
        File imageFile = new File(storageDir, imageFileName);

        // Delete existing file if it exists
        if (imageFile.exists()) {
            boolean deleted = imageFile.delete();
            android.util.Log.d("ProfileFragment", "Deleted existing profile image: " + deleted);
        }

        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush(); // Ensure data is written
        } finally {
            inputStream.close();
        }

        // Verify the file was created and has content
        if (!imageFile.exists() || imageFile.length() == 0) {
            throw new Exception("Failed to save image file properly");
        }

        android.util.Log.d("ProfileFragment", "Image saved successfully. Size: " + imageFile.length() + " bytes");
        return imageFile.getAbsolutePath();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}