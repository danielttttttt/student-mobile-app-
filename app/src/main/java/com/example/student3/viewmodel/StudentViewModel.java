package com.example.student3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.Student;
import com.example.student3.repository.StudentRepository;

import java.util.List;

public class StudentViewModel extends AndroidViewModel {
    private final StudentRepository repository;
    private final LiveData<List<Student>> allStudents;

    public StudentViewModel(Application application) {
        super(application);
        repository = new StudentRepository(application);
        allStudents = repository.getAllStudents();
    }

    public LiveData<List<Student>> getAllStudents() {
        return allStudents;
    }

    public LiveData<Student> getStudentById(int studentId) {
        return repository.getStudentById(studentId);
    }

    public LiveData<List<Student>> getStudentsByDepartment(int departmentId) {
        return repository.getStudentsByDepartment(departmentId);
    }

    public LiveData<List<Student>> searchStudents(String query) {
        return repository.searchStudents(query);
    }

    public void update(Student student) {
        repository.update(student);
    }

    public void updateProfileImagePath(int studentId, String profileImagePath) {
        repository.updateProfileImagePath(studentId, profileImagePath);
    }
}