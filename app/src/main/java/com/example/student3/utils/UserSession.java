package com.example.student3.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.student3.model.Student;

public class UserSession {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_STUDENT_ID = "studentId";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_DEPARTMENT_ID = "departmentId";
    private static final String KEY_ENROLLMENT_DATE = "enrollmentDate";
    private static final String KEY_PROFILE_IMAGE_PATH = "profileImagePath";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public UserSession(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void createLoginSession(Student student) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_STUDENT_ID, student.getStudentId());
        editor.putString(KEY_FIRST_NAME, student.getFirstName());
        editor.putString(KEY_LAST_NAME, student.getLastName());
        editor.putString(KEY_EMAIL, student.getEmail());
        editor.putString(KEY_PHONE, student.getPhone());
        editor.putInt(KEY_DEPARTMENT_ID, student.getDepartmentId());
        editor.putString(KEY_ENROLLMENT_DATE, student.getEnrollmentDate());
        editor.putString(KEY_PROFILE_IMAGE_PATH, student.getProfileImagePath());
        editor.apply();
    }

    public Student getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        Student student = new Student();
        student.setStudentId(preferences.getInt(KEY_STUDENT_ID, 0));
        student.setFirstName(preferences.getString(KEY_FIRST_NAME, ""));
        student.setLastName(preferences.getString(KEY_LAST_NAME, ""));
        student.setEmail(preferences.getString(KEY_EMAIL, ""));
        student.setPhone(preferences.getString(KEY_PHONE, ""));
        student.setDepartmentId(preferences.getInt(KEY_DEPARTMENT_ID, 0));
        student.setEnrollmentDate(preferences.getString(KEY_ENROLLMENT_DATE, ""));
        student.setProfileImagePath(preferences.getString(KEY_PROFILE_IMAGE_PATH, ""));
        return student;
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    public String getCurrentUserEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getCurrentUserName() {
        String firstName = preferences.getString(KEY_FIRST_NAME, "");
        String lastName = preferences.getString(KEY_LAST_NAME, "");
        return firstName + " " + lastName;
    }

    public String getUserName() {
        return getCurrentUserName();
    }

    public int getCurrentUserId() {
        return preferences.getInt(KEY_STUDENT_ID, 0);
    }

    public int getCurrentUserDepartmentId() {
        return preferences.getInt(KEY_DEPARTMENT_ID, 0);
    }

    public void updateProfileImagePath(String profileImagePath) {
        editor.putString(KEY_PROFILE_IMAGE_PATH, profileImagePath);
        editor.apply();
    }
}
