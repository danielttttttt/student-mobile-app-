package com.example.student3.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.student3.dao.CourseDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Course;

import java.util.List;

public class CourseRepository {
    private final CourseDao courseDao;
    private final LiveData<List<Course>> allCourses;

    public CourseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        courseDao = db.courseDao();
        allCourses = courseDao.getAllCourses();
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<Course> getCourseById(int courseId) {
        return courseDao.getCourseById(courseId);
    }

    public LiveData<List<Course>> getCoursesByDepartment(int departmentId) {
        return courseDao.getCoursesByDepartment(departmentId);
    }

    public LiveData<List<Course>> getCoursesByInstructor(int instructorId) {
        return courseDao.getCoursesByInstructor(instructorId);
    }

    public LiveData<List<Course>> getCoursesBySemester(int semesterId) {
        return courseDao.getCoursesBySemester(semesterId);
    }

    public LiveData<List<Course>> searchCourses(String query) {
        return courseDao.searchCourses(query);
    }

    public LiveData<List<Course>> searchCoursesByDepartment(String query, int departmentId) {
        return courseDao.searchCoursesByDepartment(query, departmentId);
    }

    public LiveData<List<Course>> getRegisteredCoursesByStudent(int studentId) {
        return courseDao.getRegisteredCoursesByStudent(studentId);
    }

    public LiveData<List<Course>> getRegisteredCoursesByStudentAndDepartment(int studentId, int departmentId) {
        return courseDao.getRegisteredCoursesByStudentAndDepartment(studentId, departmentId);
    }

    public void insert(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> courseDao.insert(course));
    }

    public void update(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> courseDao.update(course));
    }

    public void delete(Course course) {
        AppDatabase.databaseWriteExecutor.execute(() -> courseDao.delete(course));
    }
}