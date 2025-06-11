package com.example.student3.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class Course {

    @PrimaryKey(autoGenerate = true)
    private int courseId;
    private String courseCode;
    private String title;
    private String description;
    private int creditHours;
    private Integer departmentId; // Nullable
    private Integer instructorId; // Nullable
    private Integer semesterId; // Nullable
    private int maxStudents;

    // New scheduling fields
    private String startDate; // When the course starts (e.g., "2025-01-15")
    private String endDate; // When the course ends (e.g., "2025-05-15")
    private String daysOfWeek; // Days when course meets (e.g., "MON,WED,FRI")
    private int startPeriod; // Starting period (1-8)
    private int endPeriod; // Ending period (1-8)
    private int totalWeeklyHours; // Total hours per week for this course

    @Ignore
    public Course(String courseCode, String title, String description, int creditHours,
                  Integer departmentId, Integer instructorId, Integer semesterId, int maxStudents) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.creditHours = creditHours;
        this.departmentId = departmentId;
        this.instructorId = instructorId;
        this.semesterId = semesterId;
        this.maxStudents = maxStudents;

        // Initialize scheduling fields with defaults
        this.startDate = "";
        this.endDate = "";
        this.daysOfWeek = "";
        this.startPeriod = 1;
        this.endPeriod = 1;
        this.totalWeeklyHours = creditHours; // Default to credit hours
    }

    // Constructor with scheduling information
    public Course(String courseCode, String title, String description, int creditHours,
                  Integer departmentId, Integer instructorId, Integer semesterId, int maxStudents,
                  String startDate, String endDate, String daysOfWeek, int startPeriod, int endPeriod, int totalWeeklyHours) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.creditHours = creditHours;
        this.departmentId = departmentId;
        this.instructorId = instructorId;
        this.semesterId = semesterId;
        this.maxStudents = maxStudents;
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysOfWeek = daysOfWeek;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.totalWeeklyHours = totalWeeklyHours;
    }

    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public Integer getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Integer semesterId) {
        this.semesterId = semesterId;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    // Getters and Setters for scheduling fields
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(int startPeriod) {
        this.startPeriod = startPeriod;
    }

    public int getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(int endPeriod) {
        this.endPeriod = endPeriod;
    }

    public int getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(int totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }
}