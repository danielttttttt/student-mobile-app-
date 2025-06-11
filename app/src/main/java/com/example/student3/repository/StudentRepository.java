package com.example.student3.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.student3.dao.StudentDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Student;

import java.util.List;

public class StudentRepository {
    private final StudentDao studentDao;
    private final LiveData<List<Student>> allStudents;

    public StudentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        studentDao = db.studentDao();
        allStudents = studentDao.getAllStudents();
    }

    public LiveData<List<Student>> getAllStudents() {
        return allStudents;
    }

    public LiveData<Student> getStudentById(int studentId) {
        return studentDao.getStudentById(studentId);
    }

    public LiveData<List<Student>> getStudentsByDepartment(int departmentId) {
        return studentDao.getStudentsByDepartment(departmentId);
    }

    public LiveData<List<Student>> searchStudents(String query) {
        return studentDao.searchStudents(query);
    }

    public void update(Student student) {
        AppDatabase.databaseWriteExecutor.execute(() -> studentDao.update(student));
    }

    public Student getStudentByEmail(String email) {
        return studentDao.getStudentByEmail(email);
    }

    public void updateProfileImagePath(int studentId, String profileImagePath) {
        AppDatabase.databaseWriteExecutor.execute(() -> studentDao.updateProfileImagePath(studentId, profileImagePath));
    }
}