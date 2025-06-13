package com.example.student3.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "todos",
        foreignKeys = {
                @ForeignKey(entity = Student.class, parentColumns = "studentId", childColumns = "studentId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("studentId")
        })
public class SimpleTodo {
    @PrimaryKey(autoGenerate = true)
    private int todoId;
    
    private int studentId; // Foreign key to Student
    private String title;
    private boolean isCompleted;
    private String createdDate; // When student added this todo

    // Default constructor (required by Room)
    public SimpleTodo() {
    }

    // Constructor for creating new todos
    @Ignore
    public SimpleTodo(int studentId, String title, String createdDate) {
        this.studentId = studentId;
        this.title = title;
        this.createdDate = createdDate;
        this.isCompleted = false; // Default to not completed
    }

    // Getters and Setters
    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
