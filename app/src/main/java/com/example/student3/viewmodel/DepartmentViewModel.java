package com.example.student3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.Department;
import com.example.student3.repository.DepartmentRepository;

import java.util.List;

public class DepartmentViewModel extends AndroidViewModel {
    private final DepartmentRepository repository;
    private final LiveData<List<Department>> allDepartments;

    public DepartmentViewModel(Application application) {
        super(application);
        repository = new DepartmentRepository(application);
        allDepartments = repository.getAllDepartments();
    }

    public LiveData<List<Department>> getAllDepartments() {
        return allDepartments;
    }

    public LiveData<Department> getDepartmentById(int departmentId) {
        return repository.getDepartmentById(departmentId);
    }

    public LiveData<List<Department>> searchDepartments(String query) {
        return repository.searchDepartments(query);
    }

    public void insert(Department department) {
        repository.insert(department);
    }

    public void update(Department department) {
        repository.update(department);
    }

    public void delete(Department department) {
        repository.delete(department);
    }
}
