package com.example.student3.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "departments")
public class Department {
    @PrimaryKey(autoGenerate = true)
    private int departmentId;

    @NonNull
    private String name;

    @NonNull
    private String code;

    private String description;
    private Integer headInstructorId;

    public Department(@NonNull String name, @NonNull String code, String description, Integer headInstructorId) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.headInstructorId = headInstructorId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHeadInstructorId() {
        return headInstructorId;
    }

    public void setHeadInstructorId(Integer headInstructorId) {
        this.headInstructorId = headInstructorId;
    }
}