package com.example.student3.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.student3.dao.DepartmentDao;
import com.example.student3.database.AppDatabase;
import com.example.student3.model.Department;

import java.util.List;

public class DepartmentRepository {
    private final DepartmentDao departmentDao;
    private final LiveData<List<Department>> allDepartments;

    public DepartmentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        departmentDao = db.departmentDao();
        allDepartments = departmentDao.getAllDepartments();
    }

    public LiveData<List<Department>> getAllDepartments() {
        return allDepartments;
    }

    public LiveData<Department> getDepartmentById(int departmentId) {
        return departmentDao.getDepartmentById(departmentId);
    }

    public LiveData<List<Department>> searchDepartments(String query) {
        return departmentDao.searchDepartments(query);
    }

    public void insert(Department department) {
        AppDatabase.databaseWriteExecutor.execute(() -> departmentDao.insert(department));
    }

    public void update(Department department) {
        AppDatabase.databaseWriteExecutor.execute(() -> departmentDao.update(department));
    }

    public void delete(Department department) {
        AppDatabase.databaseWriteExecutor.execute(() -> departmentDao.delete(department));
    }
}
