package com.example.student3.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "instructors",
        foreignKeys = @ForeignKey(entity = Department.class, parentColumns = "departmentId", childColumns = "departmentId", onDelete = ForeignKey.SET_NULL),
        indices = @Index("departmentId"))
public class Instructor {
    @PrimaryKey(autoGenerate = true)
    private int instructorId;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    private String email;
    private String phone;
    private Integer departmentId;
    private String profileImagePath;

    public Instructor(@NonNull String firstName, @NonNull String lastName, String email,
                      String phone, Integer departmentId, String profileImagePath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
        this.profileImagePath = profileImagePath;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
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

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    @NonNull
    public String getFullName() {
        return firstName + " " + lastName;
    }
}