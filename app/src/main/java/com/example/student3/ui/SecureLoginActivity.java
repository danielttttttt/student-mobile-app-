package com.example.student3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import com.example.student3.R;
import com.example.student3.databinding.ActivityLoginBinding;
import com.example.student3.database.AppDatabase;
import com.example.student3.dao.StudentDao;
import com.example.student3.model.Student;
import com.example.student3.utils.UserSession;
import com.example.student3.utils.PasswordUtils;
import com.example.student3.utils.LoginAttemptManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Secure LoginActivity with proper password authentication
 * 
 * SECURITY FEATURES:
 * - Password hashing with salt (SHA-256)
 * - Login attempt limiting
 * - Account lockout protection
 * - Password strength validation
 * - Async operations (no UI blocking)
 * - Database-only authentication
 */
public class SecureLoginActivity extends AppCompatActivity {

    private static final String TAG = "SecureLoginActivity";
    private ActivityLoginBinding binding;
    private StudentDao studentDao;
    private UserSession userSession;
    private LoginAttemptManager loginAttemptManager;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize components
        AppDatabase database = AppDatabase.getDatabase(this);
        studentDao = database.studentDao();
        userSession = new UserSession(this);
        loginAttemptManager = new LoginAttemptManager(this);

        setupUI();
    }

    private void setupUI() {
        setupDepartmentSpinner();
        setupFormToggle();
        setupClickListeners();
    }

    private void setupDepartmentSpinner() {
        String[] departments = {"Computer Science", "Engineering", "Business", "Medicine", "Arts"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (binding.spinnerDepartment != null) {
            binding.spinnerDepartment.setAdapter(adapter);
        }
    }

    private void setupFormToggle() {
        updateFormVisibility();

        if (binding.tvToggleForm != null) {
            binding.tvToggleForm.setOnClickListener(v -> {
                isLoginMode = !isLoginMode;
                updateFormVisibility();
                clearFormErrors();
            });
        }
    }

    private void updateFormVisibility() {
        if (isLoginMode) {
            binding.layoutLoginForm.setVisibility(View.VISIBLE);
            binding.layoutRegisterForm.setVisibility(View.GONE);
            binding.tvToggleForm.setText("Don't have an account? Register");
        } else {
            binding.layoutLoginForm.setVisibility(View.GONE);
            binding.layoutRegisterForm.setVisibility(View.VISIBLE);
            binding.tvToggleForm.setText("Already have an account? Login");
        }
        clearFormErrors();
    }

    private void clearFormErrors() {
        // Clear login form errors
        if (binding.tilEmail != null) binding.tilEmail.setError(null);
        if (binding.tilPassword != null) binding.tilPassword.setError(null);

        // Clear registration form errors
        if (binding.tilFirstName != null) binding.tilFirstName.setError(null);
        if (binding.tilLastName != null) binding.tilLastName.setError(null);
        if (binding.tilRegisterEmail != null) binding.tilRegisterEmail.setError(null);
        if (binding.tilRegisterPassword != null) binding.tilRegisterPassword.setError(null);
        if (binding.tilPhone != null) binding.tilPhone.setError(null);
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            if (validateLoginForm()) {
                authenticateUser();
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            if (validateRegistrationForm()) {
                registerUser();
            }
        });
    }

    /**
     * Validate login form with security checks
     */
    private boolean validateLoginForm() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        // Clear previous errors
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        boolean isValid = true;

        // Validate email
        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password
        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required");
            isValid = false;
        }

        // Check if account is locked
        if (isValid && loginAttemptManager.isAccountLocked(email)) {
            String lockoutMessage = loginAttemptManager.getLockoutMessage(email);
            binding.tilEmail.setError(lockoutMessage);
            Toast.makeText(this, lockoutMessage, Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    /**
     * Secure user authentication with password verification
     */
    private void authenticateUser() {
        String email = binding.etEmail.getText().toString().trim().toLowerCase();
        String password = binding.etPassword.getText().toString();

        // Show loading state
        binding.btnLogin.setEnabled(false);
        binding.btnLogin.setText("Logging in...");

        CompletableFuture.supplyAsync(() -> {
            try {
                // Get student from database
                Student student = studentDao.getStudentByEmail(email);
                if (student == null) {
                    return null; // User not found
                }

                // Verify password
                if (PasswordUtils.verifyPassword(password, student.getPasswordHash())) {
                    return student; // Authentication successful
                } else {
                    return null; // Invalid password
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during authentication", e);
                return null;
            }
        }).thenAccept(student -> {
            runOnUiThread(() -> {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText("Login");

                if (student != null) {
                    // Successful authentication
                    loginAttemptManager.recordSuccessfulLogin(email);
                    
                    // Update last login date
                    updateLastLoginDate(student);
                    
                    // Create user session
                    userSession.createLoginSession(student);
                    
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to main activity
                    Intent intent = new Intent(SecureLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Authentication failed
                    boolean shouldLock = loginAttemptManager.recordFailedAttempt(email);
                    
                    if (shouldLock) {
                        String lockoutMessage = loginAttemptManager.getLockoutMessage(email);
                        binding.tilEmail.setError(lockoutMessage);
                        Toast.makeText(this, lockoutMessage, Toast.LENGTH_LONG).show();
                    } else {
                        int attempts = loginAttemptManager.getFailedAttemptCount(email);
                        int maxAttempts = LoginAttemptManager.getMaxLoginAttempts();
                        int remaining = maxAttempts - attempts;
                        
                        binding.tilPassword.setError("Invalid email or password");
                        Toast.makeText(this, 
                            "Invalid credentials. " + remaining + " attempts remaining.", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText("Login");
                Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }

    /**
     * Validate registration form with password strength checking
     */
    private boolean validateRegistrationForm() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etRegisterEmail.getText().toString().trim();
        String password = binding.etRegisterPassword.getText().toString();
        String phone = binding.etPhone.getText().toString().trim();

        // Clear previous errors
        binding.tilFirstName.setError(null);
        binding.tilLastName.setError(null);
        binding.tilRegisterEmail.setError(null);
        binding.tilRegisterPassword.setError(null);
        binding.tilPhone.setError(null);

        boolean isValid = true;

        // Validate first name
        if (firstName.isEmpty()) {
            binding.tilFirstName.setError("First name is required");
            isValid = false;
        }

        // Validate last name
        if (lastName.isEmpty()) {
            binding.tilLastName.setError("Last name is required");
            isValid = false;
        }

        // Validate email
        if (email.isEmpty()) {
            binding.tilRegisterEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilRegisterEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate password strength
        PasswordUtils.PasswordValidationResult passwordResult = PasswordUtils.validatePasswordStrength(password);
        if (!passwordResult.isValid) {
            binding.tilRegisterPassword.setError(passwordResult.errorMessage);
            isValid = false;
        }

        // Validate phone
        if (phone.isEmpty()) {
            binding.tilPhone.setError("Phone number is required");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Register new user with secure password hashing
     */
    private void registerUser() {
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etRegisterEmail.getText().toString().trim().toLowerCase();
        String password = binding.etRegisterPassword.getText().toString();
        String phone = binding.etPhone.getText().toString().trim();
        int departmentId = binding.spinnerDepartment.getSelectedItemPosition() + 1;

        // Show loading state
        binding.btnRegister.setEnabled(false);
        binding.btnRegister.setText("Registering...");

        CompletableFuture.supplyAsync(() -> {
            try {
                // Check if email already exists
                if (studentDao.emailExists(email)) {
                    return -1; // Email already exists
                }

                // Hash password securely
                String passwordHash = PasswordUtils.hashPassword(password);
                if (passwordHash == null) {
                    return -2; // Password hashing failed
                }

                // Create student object
                Student newStudent = new Student();
                newStudent.setFirstName(firstName);
                newStudent.setLastName(lastName);
                newStudent.setEmail(email);
                newStudent.setPhone(phone);
                newStudent.setDepartmentId(departmentId);
                newStudent.setEnrollmentDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                newStudent.setPasswordHash(passwordHash);
                newStudent.setLoginAttempts(0);
                newStudent.setAccountLocked(false);
                newStudent.setLastLoginDate(null);

                // Insert into database
                long studentId = studentDao.insert(newStudent);
                return (int) studentId;
            } catch (Exception e) {
                Log.e(TAG, "Error during registration", e);
                return -3; // Registration failed
            }
        }).thenAccept(result -> {
            runOnUiThread(() -> {
                binding.btnRegister.setEnabled(true);
                binding.btnRegister.setText("Register");

                if (result > 0) {
                    Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                    // Switch to login form and clear registration form
                    clearRegistrationForm();
                    isLoginMode = true;
                    updateFormVisibility();
                } else if (result == -1) {
                    binding.tilRegisterEmail.setError("This email is already registered");
                } else if (result == -2) {
                    Toast.makeText(this, "Password processing failed. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Update student's last login date
     */
    private void updateLastLoginDate(Student student) {
        CompletableFuture.runAsync(() -> {
            try {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                studentDao.updateLastLoginDate(student.getEmail(), currentDate);
            } catch (Exception e) {
                Log.e(TAG, "Error updating last login date", e);
            }
        });
    }

    /**
     * Clear registration form
     */
    private void clearRegistrationForm() {
        binding.etFirstName.setText("");
        binding.etLastName.setText("");
        binding.etRegisterEmail.setText("");
        binding.etRegisterPassword.setText("");
        binding.etPhone.setText("");
        binding.spinnerDepartment.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
