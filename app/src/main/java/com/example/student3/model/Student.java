package com.example.student3.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "students",
        indices = {@Index(value = "email", unique = true)})
public class Student {
    @PrimaryKey(autoGenerate = true)
    private int studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String enrollmentDate;
    private int departmentId;
    private String profileImagePath; // Add this field for profile image
    private String passwordHash; // Secure password storage
    private int loginAttempts; // Track failed login attempts
    private String lastLoginDate; // Track last successful login
    private boolean accountLocked; // Account lockout status

    // Default constructor (required by Room and for UserSession)
    public Student() {
    }

    // Constructor
    @Ignore
    public Student(String firstName, String lastName, String email, String phone, String enrollmentDate, int departmentId, String profileImagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.enrollmentDate = enrollmentDate;
        this.departmentId = departmentId;
        this.profileImagePath = profileImagePath;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    // Method to get full name by concatenating firstName and lastName
    public String getFullName() {
        return firstName + " " + lastName;
    }
}