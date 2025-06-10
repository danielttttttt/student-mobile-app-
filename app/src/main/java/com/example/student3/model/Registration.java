package com.example.student3.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "registrations",
        foreignKeys = {
                @ForeignKey(entity = Student.class, parentColumns = "studentId", childColumns = "studentId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Course.class, parentColumns = "courseId", childColumns = "courseId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("studentId"),
                @Index("courseId")
        })
public class Registration {
    @PrimaryKey(autoGenerate = true)
    private int registrationId;

    private int studentId;
    private int courseId;
    private String registrationDate;
    private String status; // "REGISTERED", "DROPPED", "COMPLETED"
    private String grade;

    public Registration(int studentId, int courseId, String registrationDate, String status, String grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.registrationDate = registrationDate;
        this.status = status;
        this.grade = grade;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}