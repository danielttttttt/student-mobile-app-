package com.example.student3.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.student3.dao.SemesterDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Semester;

import java.util.List;

public class SemesterRepository {
    private final SemesterDao semesterDao;
    private final LiveData<List<Semester>> allSemesters;

    public SemesterRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        semesterDao = db.semesterDao();
        allSemesters = semesterDao.getAllSemesters();
    }

    public LiveData<List<Semester>> getAllSemesters() {
        return allSemesters;
    }

    public LiveData<Semester> getSemesterById(int semesterId) {
        return semesterDao.getSemesterById(semesterId);
    }

    public LiveData<Semester> getCurrentSemester() {
        return semesterDao.getCurrentSemester();
    }

    public void insert(Semester semester) {
        AppDatabase.databaseWriteExecutor.execute(() -> semesterDao.insert(semester));
    }

    public void update(Semester semester) {
        AppDatabase.databaseWriteExecutor.execute(() -> semesterDao.update(semester));
    }

    public void delete(Semester semester) {
        AppDatabase.databaseWriteExecutor.execute(() -> semesterDao.delete(semester));
    }
}
