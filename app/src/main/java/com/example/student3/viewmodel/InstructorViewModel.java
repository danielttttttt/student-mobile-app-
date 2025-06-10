package com.example.student3.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.student3.model.Instructor;
import com.example.student3.repository.InstructorRepository;

import java.util.List;

public class InstructorViewModel extends AndroidViewModel {
    private final InstructorRepository repository;
    private final LiveData<List<Instructor>> allInstructors;

    public InstructorViewModel(Application application) {
        super(application);
        repository = new InstructorRepository(application);
        allInstructors = repository.getAllInstructors();
    }

    public LiveData<List<Instructor>> getAllInstructors() {
        return allInstructors;
    }

    public LiveData<Instructor> getInstructorById(int instructorId) {
        return repository.getInstructorById(instructorId);
    }

    public LiveData<List<Instructor>> getInstructorsByDepartment(int departmentId) {
        return repository.getInstructorsByDepartment(departmentId);
    }

    public LiveData<List<Instructor>> searchInstructors(String query) {
        return repository.searchInstructors(query);
    }

    public void insert(Instructor instructor) {
        repository.insert(instructor);
    }

    public void update(Instructor instructor) {
        repository.update(instructor);
    }

    public void delete(Instructor instructor) {
        repository.delete(instructor);
    }
}
