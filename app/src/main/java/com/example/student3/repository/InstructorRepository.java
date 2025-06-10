package com.example.student3.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.student3.dao.InstructorDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Instructor;

import java.util.List;

public class InstructorRepository {
    private final InstructorDao instructorDao;
    private final LiveData<List<Instructor>> allInstructors;

    public InstructorRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        instructorDao = db.instructorDao();
        allInstructors = instructorDao.getAllInstructors();
    }

    public LiveData<List<Instructor>> getAllInstructors() {
        return allInstructors;
    }

    public LiveData<Instructor> getInstructorById(int instructorId) {
        return instructorDao.getInstructorById(instructorId);
    }

    public LiveData<List<Instructor>> getInstructorsByDepartment(int departmentId) {
        return instructorDao.getInstructorsByDepartment(departmentId);
    }

    public LiveData<List<Instructor>> searchInstructors(String query) {
        return instructorDao.searchInstructors(query);
    }

    public void insert(Instructor instructor) {
        AppDatabase.databaseWriteExecutor.execute(() -> instructorDao.insert(instructor));
    }

    public void update(Instructor instructor) {
        AppDatabase.databaseWriteExecutor.execute(() -> instructorDao.update(instructor));
    }

    public void delete(Instructor instructor) {
        AppDatabase.databaseWriteExecutor.execute(() -> instructorDao.delete(instructor));
    }
}
