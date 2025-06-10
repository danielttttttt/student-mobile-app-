package com.example.student3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.Course;
import com.example.student3.repository.CourseRepository;

import java.util.List;

public class CourseViewModel extends AndroidViewModel {
    private final CourseRepository repository;
    private final LiveData<List<Course>> allCourses;

    public CourseViewModel(Application application) {
        super(application);
        repository = new CourseRepository(application);
        allCourses = repository.getAllCourses();
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<Course> getCourseById(int courseId) {
        return repository.getCourseById(courseId);
    }

    public LiveData<List<Course>> getCoursesByDepartment(int departmentId) {
        return repository.getCoursesByDepartment(departmentId);
    }

    public LiveData<List<Course>> getCoursesByInstructor(int instructorId) {
        return repository.getCoursesByInstructor(instructorId);
    }

    public LiveData<List<Course>> getCoursesBySemester(int semesterId) {
        return repository.getCoursesBySemester(semesterId);
    }

    public LiveData<List<Course>> searchCourses(String query) {
        return repository.searchCourses(query);
    }

    public LiveData<List<Course>> getRegisteredCoursesByStudent(int studentId) {
        return repository.getRegisteredCoursesByStudent(studentId);
    }

    public LiveData<List<Course>> getRegisteredCoursesByStudentAndDepartment(int studentId, int departmentId) {
        return repository.getRegisteredCoursesByStudentAndDepartment(studentId, departmentId);
    }

    public LiveData<List<Course>> searchCoursesByDepartment(String query, int departmentId) {
        return repository.searchCoursesByDepartment(query, departmentId);
    }
}